
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import DNA.Helper;
import DNA.IVersion;
import DNA.Core.StateUpdateTransaction;
import DNA.Core.Transaction;
import DNA.Network.Rest.RestException;
import DNA.Network.Rest.RestNode;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.AccountInfo;
import DNA.sdk.info.asset.AssetInfo;
import DNA.sdk.info.asset.UTXOInfo;
import DNA.sdk.info.transaction.TransactionInfo;
import DNA.sdk.wallet.UserWalletManager;

/**
 * DNA sdk 测试Demo
 * 
 * @author 12146
 *
 */
public class DNASDKDemo {

	public static void main(String[] args) throws Exception {
		Arrays.stream(IVersion.versionInfo).forEach(p -> print(p));
		// 实例化账户管理类
		String path = "./a04.db3";			// 钱包文件
		String url = "http://139.196.115.69:20334";		// DNA rest // zx
		String token = "";	// 访问令牌，可从认证服务器获取
		UserWalletManager wm = UserWalletManager.getWallet(path, url, token);
		
		wm.startSyncBlock();
		
		// 创建多个账户
		List<String> list = wm.createAccount(3);
		String addr1 = list.get(0);
		String addr2 = list.get(1);
		String addr3 = list.get(2);
		int hh = wm.blockHeight();
		print("path:"+path+",url:"+url+",hh:"+hh+",now:"+now());
		print("user1:"+addr1 + ","+wm.address2UInt160(addr1) + ",addr.len="+addr1.length()+",uint160.len="+wm.address2UInt160(addr1).length());
		print("user2:"+addr2 + ","+wm.address2UInt160(addr2));
		print("user3:"+addr3 + ","+wm.address2UInt160(addr3));
		
		boolean flag = true;
		if(!flag) {
			Transaction tx = null;
			String txHex = null;
			boolean rr = false;
			// 构造交易Reg
			tx = wm.createRegTx(addr1, "JF003", 1000, null, addr1, 0);
			// 交易签名
			txHex = wm.signTx(tx);
			
			// 发送交易
			rr = wm.sendTx(txHex);
			//
			System.out.println("rs1:"+rr+",txid:"+tx.hash().toString());Thread.sleep(1000*10);
			
			// 资产编号
			String assetid = tx.hash().toString();
			
			// 构造交易Iss
			tx = wm.createIssTx(addr1, assetid, 100, addr2, null);//"u1 分发给 u2");
			// 交易签名
			txHex = wm.signTx(tx);
			// 发送交易
			rr = wm.sendTx(txHex);
			//
			System.out.println("rs2:"+rr+",txid:"+tx.hash().toString());
			Thread.sleep(1000*10);
			
			// 构造交易Trf
			tx = wm.createTrfTx(addr2, assetid, 11, addr3, null);//"u2 分发给 u3");
			// 交易签名
			txHex = wm.signTx(tx);
			// 发送交易
			rr = wm.sendTx(txHex);
			//
			System.out.println("rs3:"+rr+",txid:"+tx.hash().toString());Thread.sleep(1000*10);
			
			// 账户管理器
			System.out.println("#######################################################################################");
		}
		if(flag) {
			// 获取utxo
			String address = "AdEmWiQBDfYMkW6Eew2J8YxPRAAseDmq1U";
			String assetid = "6146c60e384c0a0c1e276cc097cac73da98875b905a50d0cdf89bdffbdfc8aa0";
			List<UTXOInfo> utxo = wm.getUTXOs(address, assetid);
			System.out.println("utxo:"+utxo);
			// 获取balance
			long balance = wm.getBalance(address);
			System.out.println("balance:"+balance);
			
		}
		if(flag) {
			// 初始化参数
			String priKey = "d46f336479abfef0fa4bcfbaa0268138b39fe5e55fc0f2b529ca2e8eb2ba9d91";
			System.out.println("len:"+Helper.hexToBytes(priKey).length);
			String user = wm.createAccount(priKey);
			String namespace = "tsA11";
			String key = "tsA12";
			String value = "tsA13";
			// 构造交易
			StateUpdateTransaction tx = wm.createStateUpdateTx(namespace, key, value, user);
			// 交易签名
			String txHex = wm.signTx(tx);
			// 发送交易
			boolean rr = wm.sendTx(txHex);
			//
			System.out.println("rs5:"+rr+",txid:"+tx.hash().toString());Thread.sleep(1000*10);
			
			// 查询验证...
			String txid = "4086a8e90c845681e1a2c95d275e158381e04cd61760d862eda4a66a5ac29bae";
			RestNode restNode = new RestNode("http://139.196.115.69:20334");
			Transaction newtx = restNode.getRawTransaction(txid);
			if(newtx instanceof StateUpdateTransaction) {
				StateUpdateTransaction tt = (StateUpdateTransaction) newtx;
				System.out.println("#################################################");
				System.out.println("query.namespace="+new String(tt.namespace));
				System.out.println("query.key="+new String(tt.key));
				System.out.println("query.value="+new String(tt.value));
			} else {
				System.out.println("no print");
			}
		}
		
		
		if(!flag) {
		// 注册资产(资产控制者为addr1)
		print("test regAsset..............................................[st]");
//		String txid0 = wm.reg(addr1, "Token001", 10000, "用户1注册资产S01");
		String txid0 = wm.reg(addr1, "Token001", 10000, null, addr1, 0);
		print("test regAsset..............................................[ed],txid="+txid0);
		
		// 分发资产(账户addr1分发资产给addr2)
		print("test issAsset..............................................[st]");
//		String txid1 = wm.iss(addr1, txid0, 100, addr2, "用户1 分发给 用户2");
		String txid1 = wm.iss(addr1, txid0, 100, addr2, null);
		print("test issAsset..............................................[ed],txid="+txid1);
		// 转移资产(账户addr2转移资产只addr3)
		print("test trfAsset..............................................[st]");
		String txid2 = wm.trf(addr2, txid0, 11, addr3, "用户2转账给用户3");
//		String txid2 = wm.trf(addr2, txid0, 11, addr3, null);
		print("test trfAsset..............................................[ed],txid="+txid2);
		
		// 存证
		print("test storeCert..............................................[st]");
		print("start to storeCert");
		String content = ("ts_"+new Date()), desc = null;//"this is a test msg for storeCert";
		String txid3 = wm.storeCert(content, desc);
		print("finished, store............................"+txid3);
		print("test storeCert..............................................[ed],txid="+txid3+",len="+txid3.length());
		
		// 取证
		print("test queryCert..............................................[st]");
		String newContent = wm.queryCert(txid3);
		print(String.format("\told=%s\n\tnew=%s", content, newContent));
		print("test queryCert..............................................[ed],txid=end");
		}
	}
	
	public static void print(String ss) {
		System.out.println(now() + ss);
	}
	public static String now() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " ";
	}
	
	
	public void testCreateAccount() {
		// 打开账户管理器
		String path = "./dat/tsGo_01.db3";
		String url = "http://localhost:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
		// 创建账户
		String user01 = wm.createAccount();			// 创建单个账户
		List<String> list = wm.createAccount(10); 	// 批量创建10个账户
		System.out.println("user:"+user01);
		System.out.println("list:"+list);
	}
	public void testRegAsset() throws Exception {
		// 打开账户管理器
		String path = "./dat/tsGo_01.db3";
		String url = "http://localhost:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
		// 注册资产
		String issuer= "";			// 资产发行者地址
		String name = "";			// 资产名称
		long amount = 10000;		// 资产数量
		String desc = "";			// 描述
		String controller = "";		// 资产控制者地址
		int precision = 0;
		String assetid = wm.reg(issuer, name, amount , desc, controller, precision);
		System.out.println("rs:"+assetid);
	}
	public void testIssAsset() throws Exception {
		// 打开账户管理器
		String path = "./dat/tsGo_01.db3";
		String url = "http://localhost:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
		// 分发资产
		String controller= "";		// 资产控制者地址
		String assetid = "";		// 资产编号(由注册资产产生)
		long amount = 100;			// 分发数量
		String recver = "";			// 分发资产接收者地址
		String desc = "";			// 描述
		String txid = wm.iss(controller, assetid, amount , recver , desc );
		System.out.println("rs:"+txid);
	}
	public void testTrfAsset() throws Exception {
		// 打开账户管理器
		String path = "./dat/tsGo_01.db3";
		String url = "http://localhost:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(path, url, accessToken);
		// 转移资产
		String controller= "";		// 资产控制者地址
		String assetid = "";		// 资产编号(由注册资产产生)
		long amount = 100;		// 转移数量
		String recver = "";		// 转移资产接收者地址
		String desc = "";		// 描述
		String txid = wm.trf(controller, assetid, amount , recver , desc );
		System.out.println("rs:"+txid);
	}
	
	public static void testStoreCert() throws Exception {
		// 打开账户管理器
		String url = "https://www.triclouds.cn:8443";
		String accessToken = "1";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
		// 存证
		String content = "ts";		// 待存储的信息
		String desc = "dd";			// 描述
		String txid = wm.storeCert(content, desc);
		System.out.println("rs:"+txid);
	}
	
	public void testQueryCert() throws Exception {
		// 打开账户管理器
		String url = "http://localhost:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
		// 取证
		String txid = "";		// 存证编号
		String contetn= wm.queryCert(txid);
		System.out.println("rs:"+contetn);
	}
	
	public static void testAccountInfo() {
		// 打开账户管理器
		String path = "./dat/tsGoV1.0_11.db3";
		UserWalletManager wm = UserWalletManager.getWallet(path);
		// 查询账户信息
		String userAddr = "AZg3vyDawyHtET8tNhs1odKPa6yy8qFgxK";		// 账户地址
		AccountInfo info = wm.getAccountInfo(userAddr);
		System.out.println("rs:"+info);
	}
	public static void testAccountAsset() {
		// 打开账户管理器
		String path = "./dat/tsGoV1.0_11.db3";
		UserWalletManager wm = UserWalletManager.getWallet(path);
		// 查询账户资产
		String userAddr = "AZg3vyDawyHtET8tNhs1odKPa6yy8qFgxK";
		AccountAsset info = wm.getAccountAsset(userAddr);
		System.out.println("rs:"+info);
	}
	public static void testAssetInfo() throws RestException {
		// 打开账户管理器
//		String url = "http://127.0.0.1:20334";
		String url = "http://192.168.1.195:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
		// 查询账户资产
		String assetid = "c35d2195b197f8f75a2fb0367d667d2f63a02ba9e1929ab6378ffe91218f0446";
		AssetInfo info = wm.getAssetInfo(assetid);
		System.out.println("rs:"+info);
	}
	public static void testTransactionInfo() throws Exception {
		// 打开账户管理器
		String url = "http://52.80.21.194:20334";
		String accessToken = "";				// 从认证服务器获取该访问令牌
		UserWalletManager wm = UserWalletManager.getWallet(url, accessToken);
		// 查询账户资产
		String txid = "05eee614559bf42dbedb2b062a6d5ef6b813abcc130ed3f55b3d0cdfec8c86ad";
		TransactionInfo info = wm.getTransactionInfo(txid);
		System.out.println("rs:"+info);
	}
}

/**
 out:
 *******************************************************************************************
2017-07-11 17:34:43.896 project:DNA SDK
2017-07-11 17:34:43.897 version:sdk-v0.6
2017-07-11 17:34:43.898 description:This version is suitable for DNA-v0.6alpha,  named sdk-v0.6
2017-07-11 17:34:43.898 lastModified:2017-07-10
2017-07-11 17:34:43.898 author:ts
2017-07-11 17:34:45.466 path:./a02.db3,url:http://139.196.115.69:20334,hh:952,now:2017-07-11 17:34:45.466 
2017-07-11 17:34:45.467 user1:AGHjqsvApP6NpvY49t4C1d6AP5A7vrDyMk,d04088a1b313317a10ed1060f00321bfeef6a505,addr.len=34,uint160.len=40
2017-07-11 17:34:45.467 user2:AeCNvkKeDpcbERhS7dpeH2oNv7B2Gkhf5q,c648df5cab60fcf2ae8fd0c4e17ee35a3231f5f5
2017-07-11 17:34:45.467 user3:Abb63wtnHB4UVdFJA9X4kNmEmRVWtqqyjc,7b754ebd297783968d36d03db90945f56f4b58d9
POST url=http://139.196.115.69:20334/api/v1/transaction?access_token=&auth_type=OAuth2.0, body={"Action":"sendrawtransaction","Type":"t001","Version":"v001","Data":"4000054a4630303308110000e8764817000000205217ad450ae190c361a190efa2e688f40491be540b566656c68e99720454724b202fcdcb0de964d9f1587a154945da18fcb261be5da810c04a0031f24779ea9b1805a5f6eebf2103f06010ed107a3113b3a18840d0000000014140656ee08c1eb6a9203aa7bf9315325b5e5654e097322ef24243538a09632598c091cf6d0801e1349193bb30c1f015f65594b12030fa8c1a5c1a16507344cb60052321025217ad450ae190c361a190efa2e688f40491be540b566656c68e99720454724bac"}
rs1:true,txid:7eb4f1628e1e659c187875d73fc16a08a94852fbdc0824b48a0bfd70f99ecabd
POST url=http://139.196.115.69:20334/api/v1/transaction?access_token=&auth_type=OAuth2.0, body={"Action":"sendrawtransaction","Type":"t001","Version":"v001","Data":"0100000001bdca9ef970fd0b8ab42408dcfb5248a9086ac13fd77578189c651e8e62f1b47e00e40b5402000000f5f531325ae37ee1c4d08faef2fc60ab5cdf48c6014140baff21bc596ac73ec49903c53b0a7b954a8196e26931eeb01fed7a192d07947de7a5222841c36c0b26d2247816c022e8626088721684c22a78ee08610859eea32321025217ad450ae190c361a190efa2e688f40491be540b566656c68e99720454724bac"}
rs2:true,txid:3ad650eeeae7570a8af83c39c487c99338c985f9e0531e88b0be070d9998c135
POST url=http://139.196.115.69:20334/api/v1/transaction?access_token=&auth_type=OAuth2.0, body={"Action":"sendrawtransaction","Type":"t001","Version":"v001","Data":"8000000135c198990d07beb0881e53e0f985c93893c987c4393cf88a0a57e7eaee50d63a000002bdca9ef970fd0b8ab42408dcfb5248a9086ac13fd77578189c651e8e62f1b47e00ab904100000000d9584b6ff54509b93dd0368d96837729bd4e757bbdca9ef970fd0b8ab42408dcfb5248a9086ac13fd77578189c651e8e62f1b47e00397b1202000000f5f531325ae37ee1c4d08faef2fc60ab5cdf48c601414094e5f2266c84c79686550851ae190b095fa0275307b81a5ad1c5091141f3ce8bc916b5e039b4dd48528cdb5a164558c2d3615a6c32a088a740f9687b096e50f8232102925eaef9d492c8c1fedf8e26f510ab6f4626af668b6e4dca9610b7c42bbec6b8ac"}
rs3:true,txid:174560a6c14dc1165e4df007e96c0337874beb27c684a5463c433704e47d5f54
#######################################################################################
len:32
POST url=http://139.196.115.69:20334/api/v1/transaction?access_token=&auth_type=OAuth2.0, body={"Action":"sendrawtransaction","Type":"t001","Version":"v001","Data":"900005747341313105747341313205747341313320d1708024559a9a83028c25c7f0bf12b0a01ea8ea9cff077748bc54d624f62f6520b4e3f0b3f5c65b5e46eac24be33f9d62dff8a6973f376892b5a5cd79b8100ec6000000014140b571184d8783f38305c0f59006aa62f5ac6a4562cdac9faf42d69d4546c8c8f8cfe61bad3bfe1e1d225d984bb677d3b23360acf33247b1b7107dbfb2b18d5a38232102d1708024559a9a83028c25c7f0bf12b0a01ea8ea9cff077748bc54d624f62f65ac"}
rs5:true,txid:036bc4191a55063fb0b8513760794271f820822e3bccf4a0a2a2eecad6c7e6ef
#################################################
query.namespace=tsA01
query.key=tsA02
query.value=tsA03

 *
 */
