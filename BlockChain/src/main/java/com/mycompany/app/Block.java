package com.mycompany.app;

import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Date;

public class Block {
	private int index;
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getPreviousHash() {
		return previousHash;
	}
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	private String hash;
	private String previousHash;
	private String data;
	//private List<Transaction> transaction;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will 
	private long timeStamp;
	private int nonce;
	public String merkleRoot;
	
	
	public Block() {
		super();
	}
//	public Block (int index, long timeStamp,String data, int nonce,String previousHash, String hash) {
//		super();
//		this.setIndex(index);
//		this.timeStamp = timeStamp;
//		this.data = data;
//		this.nonce = nonce;
//		this.previousHash = previousHash;
//		this.hash = hash;
//		
//	}
	
	//Block Constructor.  
		public Block(String previousHash ) {
			this.previousHash = previousHash;
			this.timeStamp = new Date().getTime();
			
			this.hash = calHash(); //Making sure we do this after we set the other values.
		}

	

//	public String calHash() {
//		String calhash = HashUtil.applySha256(Integer.toString(index)+
//				 Long.toString(timeStamp)+data
//				+ Integer.toString(nonce)+previousHash 
//				);
//				
//		return calhash;
//		
//	}

	
		//Calculate new hash based on blocks contents
		public String calHash() {
			String calculatedhash = HashUtil.applySha256( 
					previousHash +
					Long.toString(timeStamp) +
					Integer.toString(nonce) + 
					merkleRoot
					);
			return calculatedhash;
		}


		//Increases nonce value until hash target is reached.
		public void mineBlock(int difficulty) {
			merkleRoot = HashUtil.getMerkleRoot(transactions);
			String target = HashUtil.getDificultyString(difficulty); //Create a string with difficulty * "0" 
			while(!hash.substring( 0, difficulty).equals(target)) {
				nonce ++;
				hash = calHash();
			}
			System.out.println("Block Mined!!! : " + hash);
		}
		
		//Add transactions to this block
		public boolean addTransaction(Transaction transaction) {
			//process transaction and check if valid, unless block is genesis block then ignore.
			if(transaction == null) return false;		
			if((previousHash != "0")) {
				if((transaction.processTransaction() != true)) {
					System.out.println("Transaction failed to process. Discarded.");
					return false;
				}
			}
			transactions.add(transaction);
			System.out.println("Transaction Successfully added to Block");
			return true;
		}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
