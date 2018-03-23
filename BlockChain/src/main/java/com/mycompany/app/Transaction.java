package com.mycompany.app;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


//每笔交易将携带一定以下信息：
//1.资金付款人的公匙信息。
//2.资金收款人的公匙信息。
//3.被转移资金的金额。
//4.输入，它是对以前的交易的引用，证明发送者有资金发送。
//5.输出，显示交易中收款方相关地址数量。(这些输出被引用为新交易的输入)
//6.一个加密签名，证明该交易是由地址的发送者是发送的，并且数据没有被更改。(阻止第三方机构更改发送的数量)

public class Transaction {

	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey reciepient; //Recipients address/public key.
	public float value; //migrate token amount
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet and 
	//change the right sender address;
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender=from;
		this.reciepient=to;
		this.value=value;
		this.inputs=inputs;
		
	}
	
	
	// This Calculates the transaction hash (which will be used as its Id)
	private String calTransactionId() {
		sequence++;  //increase the sequence to avoid 2 identical transactions having the 
		return HashUtil.applySha256(HashUtil.getStringFromKey(sender)
				+HashUtil.getStringFromKey(reciepient) + 
				Float.toString(value)+sequence
				);
	}

	//Signs all the data we dont wish to be tampered with.
	//签名将由矿工验证，只有签名验证成功后交易才能被添加到区块中去。
	public void generateSignature(PrivateKey privateKey) {
		String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		signature = HashUtil.applyECDSASig(privateKey,data);		
	}
	//Verifies the data we signed hasnt been tampered with
	//当我们检查区块链的有效性时，我们也可以检查签名
	public boolean verifiySignature() {
		String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		return HashUtil.verifyECDSASig(sender, data, signature);
	}
	
	//通过这种方法，我们执行一些检查以确保交易有效，然后收集输入并生成输出。
//	最重要的是，最后，我们抛弃了输入在我们的UTXO列表，这就意味着一个可以使用的交易输出必须之前一定是输入，
//	所以输入的值必须被完全使用，所以付款人必须改变它们自身的金额状态。
	//Returns true if new transaction could be created.	
	public boolean processTransaction() {
			
			if(verifiySignature() == false) {
				System.out.println("#Transaction Signature failed to verify");
				return false;
			}
					
			//gather transaction inputs (Make sure they are unspent):
			for(TransactionInput i : inputs) {
				i.UTXO = AugusChain.UTXOs.get(i.transactionOutputId);
			}

			//check if transaction is valid:
			if(getInputsValue() < AugusChain.minimumTransaction) {
				System.out.println("#Transaction Inputs to small: " + getInputsValue());
				return false;
			}
			
			//generate transaction outputs:
			float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
			transactionId = calTransactionId();
			outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
			outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
					
			//add outputs to Unspent list
			for(TransactionOutput o : outputs) {
				AugusChain.UTXOs.put(o.id , o);
			}
			
			//remove transaction inputs from UTXO lists as spent:
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				AugusChain.UTXOs.remove(i.UTXO.id);
			}
			
			return true;
		}
		
	//returns sum of inputs(UTXOs) values
		public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				total += i.UTXO.value;
			}
			return total;
		}

	//returns sum of outputs:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
		}


}
