package com.mycompany.app;

public class BlockTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Block firstBlock = new Block(1,System.currentTimeMillis(),"genesisBlock gererate",1,"111","000");
		System.out.println(firstBlock.getHash());
		firstBlock.mineBlock(2);

	}

}
