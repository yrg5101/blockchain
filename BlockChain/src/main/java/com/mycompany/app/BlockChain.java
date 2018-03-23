package com.mycompany.app;

import java.util.ArrayList;

public class BlockChain {
	public static ArrayList<Block> blockChainList = new ArrayList();
	public static int difficulty = 3;

	
	
	
	
	//检查区块的合理性 
	public static Boolean isChainVaild() {
		Block currentBlock;
		Block previousBlock;
		
		for (int i = 1; i < blockChainList.size(); i++) {
			currentBlock = blockChainList.get(i);
			previousBlock = blockChainList.get(i-1);
			
			if (!currentBlock.getHash().equals(currentBlock.calHash())) {
				System.out.println("Current Hashes not equal");		
//				System.out.println("Current Hashes:" + currentBlock.getHash());	
//				System.out.println("calHash Hashes:" + currentBlock.calHash());	
				return false;
			}
			
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Block genesisBlock = new Block(1,System.currentTimeMillis(),"genesisBlock gererate",1,"111","000");
		blockChainList.add(genesisBlock);
//		System.out.println(JsonUtil.toJson(blockChainList));
		
		
//		List<Transaction> txs = new Transaction<>();
//		Transaction tx1 = new Transaction();
//		Transaction tx2 = new Transaction();
//		Transaction tx3 = new Transaction();
//		txs.add(tx1);
//		txs.add(tx2);
//		txs.add(tx3);
//		
//		Transaction sysTx = new Transaction();
//		txs.add(sysTx);
		
		
		Block lasterBlock = blockChainList.get(blockChainList.size()-1);
		
		String hash ="";
		String target = new String(new char[difficulty]).replace('\0', '0');
		int nonce = 1;
		while (true) {
			hash = HashUtil.applySha256(Integer.toString(lasterBlock.getIndex()+1)+
					Long.toString(System.currentTimeMillis())+"second block"+
					Integer.toString(nonce)+lasterBlock.getHash());
//			hash = lasterBlock.calHash();
			if (hash.substring(0,difficulty).equals(target)) {
				System.out.println("挖矿成功！！！" + nonce + "HASH : "+ hash );
				break;
			}
			nonce++;
			System.out.println("挖矿失败！！！"  + "HASH : "+ hash );
		}
		
		
		Block newBlock = new Block(lasterBlock.getIndex()+1,System.currentTimeMillis(),"second block",nonce,lasterBlock.getHash(),hash);
		blockChainList.add(newBlock);
		System.out.println("挖矿后的区块链：" + JsonUtil.toJson(blockChainList));
		
		System.out.println("\nBlockchain is Valid: " + isChainVaild());

		
	}

}
