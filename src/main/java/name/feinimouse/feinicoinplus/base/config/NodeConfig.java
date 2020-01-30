package name.feinimouse.feinicoinplus.base.config;

import name.feinimouse.feinicoinplus.base.node.BaseCenter;
import name.feinimouse.feinicoinplus.base.node.BaseClasCenter;
import name.feinimouse.feinicoinplus.base.node.BaseOrder;
import name.feinimouse.feinicoinplus.base.node.BaseVerifier;
import name.feinimouse.feinicoinplus.core.node.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;

@Configuration
public class NodeConfig extends BaseConfig {
    
    @Value("${NODE_INTERVAL}")
    protected long NODE_INTERVAL;

    @Value("${FETCH_INTERVAL}")
    protected long FETCH_INTERVAL;
    
    @Value("${COLLECT_INTERVAL}")
    protected long COLLECT_INTERVAL;
    
    @Value("${CACHE_CLASSICAL}")
    protected int CACHE_CLASSICAL;
    
    private <T extends AbstractNode> T initNode(T node) {
        String address = addressManager.getAddress();
        KeyPair keyPair = signGenerator.genKeyPair();
        publicKeyHub.setKey(address, keyPair.getPublic());
        node.setAddress(address);
        node.setPrivateKey(keyPair.getPrivate());
        node.setNetwork(nodeNetwork);
        node.setTaskInterval(NODE_INTERVAL);
        nodeNetwork.registerNode(node);
        return node;
    }
    
    @Bean
    public Order order() {
        BaseOrder order = new BaseOrder();
        order.setVerifiersAddress(verifier().getAddress());
        return initNode(order);
    }

    @Bean
    public Verifier verifier() {
        BaseVerifier verifier = new BaseVerifier(verifierCore);
        return initNode(verifier);
    }

    @Bean
    public FetchCenter fetchCenter() {
        FetchCenter center = new BaseCenter(centerCore);
        center.setFetchInterval(FETCH_INTERVAL);
        center.setOrdersAddress(order().getAddress());
        return initNode(center);
    }
    
    @Bean
    public ClassicalCenter classicalCenter() {
        ClassicalCenter center = new BaseClasCenter(centerCore, verifierCore);
        center.setCollectInterval(COLLECT_INTERVAL);
        center.setCacheWaitMax(CACHE_CLASSICAL);
        return initNode(center);
    }
}
