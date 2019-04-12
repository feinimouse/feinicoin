package name.feinimouse.feinicoin.account;

import name.feinimouse.feinicoin.block.MerkelTreeNode;

// 账户信息每次由中央节点进行写入
public abstract class Account implements MerkelTreeNode {
    // 账户的hash
    protected String hash;
    // 账户的数字货币
    protected double coin;
    // 账户的公钥
    protected String key;
    // 上一次改变引用的区块
    protected long preBlockNum;
    // 账户是否发生过改变
    protected boolean changed;
}