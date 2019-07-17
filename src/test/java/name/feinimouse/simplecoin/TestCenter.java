package name.feinimouse.simplecoin;

import name.feinimouse.feinicoin.account.Transaction;
import name.feinimouse.simplecoin.block.*;
import name.feinimouse.simplecoin.manager.SimplePureAccountCenter;
import name.feinimouse.simplecoin.manager.SimplePureAccountOrder;
import name.feinimouse.utils.LoopUtils;
import net.openhft.hashing.LongHashFunction;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.security.SignatureException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TestCenter extends SetupTest {
    private SimplePureAccountCenter center;
    private SimplePureAccountOrder order;
    private List<Transaction> transList;
    private SimpleHeader testHeader;
    
    @Before
    public void setUp() throws SignatureException {
        transList = LoopUtils.loopToList(1000, transGen::genSignedTrans);
        order = new SimplePureAccountOrder(userManager, transList);
        center = new SimplePureAccountCenter(order);

        // 生成区块头
        testHeader = new SimpleHeader();
        testHeader.setNumber(0);
        testHeader.setPreHash("0000000000");
        testHeader.setTimestamp(System.currentTimeMillis());
        testHeader.setProducer("Simple Center");
        testHeader.setTransRoot("xxxxxxxx");
        testHeader.setAssetRoot("xxxxxxxx");
        testHeader.setAccountRoot("xxxxxxxx");
        testHeader.setVersion("0.0.1");
        
        var sign = new SimpleSign();
        sign.setSign("center", sm2.signToByte(testHeader.toJson().toString()));
        testHeader.setSign(sign);
        testHeader.setHash(String.valueOf(LongHashFunction.xx().hashChars(testHeader.toJson().toString())));
    }
    
    @Test
    public void testOrder() throws InterruptedException, ExecutionException {
        var startTime = System.nanoTime();
        var executor = Executors.newSingleThreadExecutor();
        var orderRes = executor.submit(order::activate);
        Thread.sleep(500);
        order.isOutBlock(false);
        Thread.sleep(500); // 这段时间休眠和验证重合了
        order.isOutBlock(true);
        Thread.sleep(500);
        order.isOutBlock(false);
        var verifyTime = orderRes.get();
        var runTime = System.nanoTime() - startTime;
        System.out.printf("验证 %d 条交易共花费：%f s \n", transList.size(), verifyTime / 1000000000f);
        System.out.printf("总运行时间：%f s \n", runTime / 1000000000f);
    }
    
    @Test
    public void testPullDB() {
        var list = MongoDao.getTransFromBlock(3);
        System.out.println(list.size());
        list.forEach(System.out::println);
    }
    
    @Test
    public void testWrite() {
        order.isOutBlock(false);
        var verifyTime = order.activate();
        // TODO 此处手动统计时间，检查区块头的生成
        var block = center.createBlock();
        center.write(block);
        System.out.printf("验证 %d 条交易共花费：%f s \n", transList.size(), verifyTime / 1000000000f);
        collectTime(center.getSaveTimes(), "出块");
    }
    
//    @Test
//    public void testCollect() throws ExecutionException, InterruptedException {
//        var executor = Executors.newFixedThreadPool(2);
//        var orderRes = executor.submit(order::activate);
//        var centerRes = executor.submit(center::collectTransaction);
//        centerRes.get();
//        var verifyTimes = orderRes.get();
//        System.out.printf("验证 %d 条交易共花费：%f s \n", transList.size(), verifyTimes / 1000000000f);
//    }
    
    @Test
    public void testCenter() {
        center.activate();
        System.out.printf("运行总时间：%f \n", center.getRunTime() / 1000000000f);
        System.out.printf("验证总时间：%f \n", center.getVerifyTime() / 1000000000f);
        super.collectTime(center.getSaveTimes(), "写入");
    }
    
}
