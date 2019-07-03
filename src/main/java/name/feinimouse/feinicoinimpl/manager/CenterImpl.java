package name.feinimouse.feinicoinimpl.manager;

import name.feinimouse.feinicoin.account.Account;
import name.feinimouse.feinicoin.account.Assets;
import name.feinimouse.feinicoin.account.Transaction;
import name.feinimouse.feinicoin.block.Block;
import name.feinimouse.feinicoin.manager.Center;
import name.feinimouse.feinicoin.manager.Order;

import java.util.List;

import lombok.Data;

/**
 * Create by 菲尼莫斯 on 2019/4/15
 * Email: cyhkkha@gmail.com
 * File name: CenterImpl
 * Program : feinicoin
 * Description :
 */
@Data
public class CenterImpl implements Center, Cloneable {
    // 节点唯一标识
    private String hash;
    // 是否轮到当前节点记账
    private boolean isActive;
    // 其他中央节点
    private List<Center> centerList;
    // 名下的所有排序节点
    private List<Order> orderList;
    // 最新的区块
    private Block latestBlock;
    // 出块时间
    public static int blockDelay = 500;

    // 当前缓存的的交易列表
    private List<Transaction> transList;
    // 当前缓存的的账户列表
    private List<Account> accountList;
    // 当前缓存的的资产列表
    private List<Assets> properList;


    @Override
    public void activate() {
        
    }

    @Override
    public Block createBlock() {
        return null;
    }

    @Override
    public void write() {

    }

    @Override
    public void broadcast() {

    }

    @Override
    public void syncBlock(Block b) {

    }

    @Override
    public String getHash() {
        return this.hash;
    }

    @Override
    public String getName() {
        return null;
    }
}
