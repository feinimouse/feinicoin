package name.feinimouse.feinicoinplus.base.sim;

import name.feinimouse.feinicoinplus.core.crypt.HashGenerator;
import name.feinimouse.feinicoinplus.core.crypt.PublicKeyHub;
import name.feinimouse.feinicoinplus.core.crypt.SignGenerator;
import name.feinimouse.feinicoinplus.core.data.Account;
import name.feinimouse.feinicoinplus.core.data.PackerArr;
import name.feinimouse.feinicoinplus.core.data.Transaction;
import name.feinimouse.feinicoinplus.core.sim.AccountManager;
import name.feinimouse.feinicoinplus.core.sim.AddressManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("accountManager")
public class ListMapAccMan implements AccountManager, InitializingBean {

    private HashGenerator hashGenerator;
    private AddressManager addressManager;
    private PublicKeyHub publicKeyHub;
    private SignGenerator signGenerator;

    private List<String> accountList;
    private Map<String, Account> accountMap;
    private Map<String, PrivateKey> keyMap;


    private Random random = new Random();

    @Value("${NUMBER_ACCOUNT}")
    private int NUMBER_ACCOUNT;

    public ListMapAccMan() {
        accountList = Collections.synchronizedList(new ArrayList<>());
        accountMap = new ConcurrentHashMap<>();
        keyMap = new ConcurrentHashMap<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        genAccount(NUMBER_ACCOUNT);
    }

    @Autowired
    public void setAddressManager(AddressManager addressManager) {
        this.addressManager = addressManager;
    }

    @Autowired
    public void setHashGenerator(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    @Autowired
    public void setPublicKeyHub(PublicKeyHub publicKeyHub) {
        this.publicKeyHub = publicKeyHub;
    }

    @Autowired
    public void setSignGenerator(SignGenerator signGenerator) {
        this.signGenerator = signGenerator;
    }

    @Override
    public void genAccount(int number) {
        for (int i = 0; i < number; i++) {
            String address = addressManager.getAddress();
            int coin = random.nextInt(100) + 500;
            Account account = new Account(address, coin);
            put(account);
            KeyPair keyPair = signGenerator.genKeyPair();
            publicKeyHub.setKey(address, keyPair.getPublic());
            keyMap.put(address, keyPair.getPrivate());
        }
    }

    @Override
    public PrivateKey getPrivateKey(String address) {
        return keyMap.get(address);
    }

    @Override
    public Account getRandom() {
        int index = random.nextInt(size());
        String address = accountList.get(index);
        return get(address);
    }

    @Override
    public Account getRandomEx(Account account) {
        Account accountNext = getRandom();
        int i = 0;
        while (account == accountNext) {
            i++;
            accountNext = getRandom();
            if (i > 50) {
                throw new RuntimeException("no enough account");
            }
        }
        return accountNext;
    }

    @Override
    public Account getRandomEx(String address) {
        Account account = get(address);
        return getRandomEx(account);
    }

    @Override
    public Account get(String address) {
        return accountMap.get(address);
    }

    @Override
    public boolean contain(String address) {
        return accountMap.containsKey(address);
    }

    @Override
    public synchronized boolean put(Account account) {
        String address = account.getAddress();
        if (contain(address)) {
            return false;
        }
        accountMap.put(address, account);
        accountList.add(address);
        return true;
    }

    @Override
    public int size() {
        return accountList.size();
    }

    @Override
    public synchronized boolean remove(String address) {
        if (contain(address)) {
            int index = accountList.indexOf(address);
            if (index >= 0) {
                accountList.remove(index);
            }
            accountMap.remove(address);
            return true;
        }
        return false;
    }

    @Override
    public PackerArr pack() {
        Account[] accounts = accountList.stream().map(accountMap::get).toArray(Account[]::new);
        return hashGenerator.hash(accounts, Account.class);
    }

    @Override
    public boolean commit(Transaction trans) {
        String sender = trans.getSender();
        String receiver = trans.getReceiver();
        if (contain(sender) && contain(receiver)) {
            Account senderAcc = get(trans.getSender());
            Account receiverAcc = get(trans.getReceiver());
            int coin = trans.getCoin();
            senderAcc.setCoin(senderAcc.getCoin() - coin);
            receiverAcc.setCoin(receiverAcc.getCoin() + coin);
            return true;
        }
        return false;
    }
}
