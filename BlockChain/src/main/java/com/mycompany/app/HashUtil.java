package com.mycompany.app;

import java.util.List;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

import com.google.gson.GsonBuilder;

public class HashUtil {

	   public static String applySha256(String input) {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(input.getBytes("UTF-8"));
	            StringBuffer hexString = new StringBuffer();
	            for (int i = 0; i < hash.length; i++) {
	                String hex = Integer.toHexString(0xff & hash[i]);
	                if (hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	            return hexString.toString();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
	   
	   
	   //Applies ECDSA Signature and returns the result ( as bytes ).
	   // applyECDSASig 方法的输入参数为付款人的私钥和需要加密的数据信息，数字签名后返回字节数组。
	    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
	        Signature dsa;
	        byte[] output = new byte[0];
	        try {
	            dsa = Signature.getInstance("ECDSA", "BC");
	            dsa.initSign(privateKey);
	            byte[] strByte = input.getBytes();
	            dsa.update(strByte);
	            byte[] realSig = dsa.sign();
	            output = realSig;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	        return output;
	    }

	    //Verifies a String signature
	    // verifyECDSASig 方法的输入参数为公钥、加密的数据和签名，调用该方法后返回true或false来说明签名是否是有效
	    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
	        try {
	            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
	            ecdsaVerify.initVerify(publicKey);
	            ecdsaVerify.update(data.getBytes());
	            return ecdsaVerify.verify(signature);
	        }catch(Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    //Short hand helper to turn Object into a json string
	    public static String getJson(Object o) {
	        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	    }

	    //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
	    public static String getDificultyString(int difficulty) {
	        return new String(new char[difficulty]).replace('\0', '0');
	    }

	    // getStringFromKey 返回任何 key 的编码字符串
	    public static String getStringFromKey(Key key) {
	        return Base64.getEncoder().encodeToString(key.getEncoded());
	    }

//	    现在我们已经有了一个有效的交易系统，我们需要把交易加入到我们的区块链中。
//	    我们把交易列表替换我们块中无用的数据，但是在一个单一的区块中可能存放了1000个交易，
//	    这就会导致大量的hash计算，不用担心在这里我们使用了交易的 merkle root 
	    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
	        int count = transactions.size();

	        List<String> previousTreeLayer = new ArrayList<String>();
	        for(Transaction transaction : transactions) {
	            previousTreeLayer.add(transaction.transactionId);
	        }
	        List<String> treeLayer = previousTreeLayer;

	        while(count > 1) {
	            treeLayer = new ArrayList<String>();
	            for(int i=1; i < previousTreeLayer.size(); i+=2) {
	                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
	            }
	            count = treeLayer.size();
	            previousTreeLayer = treeLayer;
	        }

	        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
	        return merkleRoot;
	    }

	 

	    public static void main(String[] args) {
	        System.out.println(applySha256("123"));
	    }
}
