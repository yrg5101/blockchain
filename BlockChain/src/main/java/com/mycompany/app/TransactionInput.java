package com.mycompany.app;

public class TransactionInput {
//如果你拥有1比特币，你必须前面就得接收1比特币。
//比特币的账本不会在你的账户中增加一个比特币也不会从发送者那里减去一个比特币，
//发送者只能指向他/她之前收到过一个比特币，所以一个交易输出被创建用来显示一个比特币发送给你的地址
//（交易的输入指向前一个交易的输出）。
//你的钱包余额是所有未使用的交易输出的总和

	public String transactionOutputId;//Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}


}
