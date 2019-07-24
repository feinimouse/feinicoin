package name.feinimouse.simplecoin;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import name.feinimouse.utils.LoopUtils;
import net.openhft.hashing.LongHashFunction;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionGen {
    private Random random;
    private UserManager userManager;
    private LongHashFunction xxHash;
    @Getter
    private List<Long> signTimes;
    @Getter @Setter
    private int coinLimit = 1000;

    public TransactionGen(@NonNull UserManager userManager) {
        this.userManager = userManager;
        this.random = new Random();
        this.xxHash = LongHashFunction.xx();
        this.signTimes = new ArrayList<>();
    }

    public SimpleTransaction genTransaction() {
        var sender = userManager.getRandomUser();
        var receiver = userManager.getRandomUser(sender);
        return genTransaction(sender, receiver);
    }
    
    public SimpleTransaction genTransaction(String sender, String receiver) {
        var coin = random.nextInt(coinLimit);
        var timestamp = System.currentTimeMillis();
        var trans = new SimpleTransaction(timestamp, sender, receiver, coin);
        trans.setHash(String.valueOf(xxHash.hashChars(trans.getSummary())));
        return trans;
    }

    public SimpleTransaction sign(SimpleTransaction t) throws SignatureException {
        var user = t.getSender();
        return sign(t, user);
    }
    
    public SimpleTransaction sign(@NonNull SimpleTransaction t, String user) throws SignatureException {
        var signer = userManager.getSM2(user);
        var before = System.nanoTime();
        var signRes = signer.signToByte(t.getSummary());
        signTimes.add(System.nanoTime() - before);

        var signObj = t.getSign();
        if (signObj == null) {
            signObj = new SimpleSign();
        }
        signObj.setSign("sender", signRes);
        t.setSign(signObj);
        return t;
    }

    public SimpleTransaction genSignedTrans() {
        try {
            return sign(genTransaction());
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new RuntimeException("签名错误，交易生成失败！");
        }
    }
    
    public UTXOBundle genUTXOBundle(int size) {
        var mode = random.nextInt(2);
        var bundle = new UTXOBundle();
        var user = userManager.getRandomUser();
        LoopUtils.loop(size, () -> {
            var add1 = userManager.getRandomAddress(user);
            var add2 = userManager.getRandomAddress(userManager.getRandomUser(user));
            try {
                if (mode > 0) {
                    var trans = genTransaction(add1, add2);
                    bundle.addCoin((Integer) trans.getCoin());
                    return sign(trans, add1);
                } else {
                    var trans = genTransaction(add2, add1);
                    bundle.addCoin((Integer) trans.getCoin());
                    return sign(trans, add2);
                }
            } catch (SignatureException e) {
                e.printStackTrace();
                throw new RuntimeException("生成UTXO交易出错");
            }
        });
        bundle.setOwner(user);
        bundle.setIssuer("SimpleCenter");
        bundle.setType("Test");
        bundle.setTimestamp(System.currentTimeMillis());
        return bundle;
    }
    
}