package name.feinimouse.simplecoin.account;

import name.feinimouse.simplecoin.block.Hashable;

// 交易信息由用户发起，并由检查节点节点进行审核
public interface Transaction extends Hashable {
    // 交易时间戳
    long getTimestamp();
    // 发起者
    String getSender();
    // 接收者
    String getReceiver();
    // 金额
    Number getCoin();
    // 获取签名
    Sign getSign();
    // 交易的摘要
    String getSummary();
    // 附加功能
    ExtFunc getExtFunc();
    
}