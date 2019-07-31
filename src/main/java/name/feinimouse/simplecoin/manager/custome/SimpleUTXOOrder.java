package name.feinimouse.simplecoin.manager.custome;

import lombok.NonNull;
import name.feinimouse.simplecoin.account.UTXOBundle;
import name.feinimouse.simplecoin.UserManager;
import name.feinimouse.simplecoin.manager.SimpleOrder;

import java.util.List;

public class SimpleUTXOOrder extends SimpleOrder<UTXOBundle, UTXOBundle> {
    public SimpleUTXOOrder(@NonNull UserManager manager, @NonNull List<UTXOBundle> list) {
        super(manager, list);
    }
    
    @Override
    public long activate() {
        processing = true;
        verifyTimes.clear();
        UTXOBundle bundle;
        try {
            while ((bundle = allTrans.poll()) != null) {
                final var utxoBundle = bundle;
                utxoBundle.forEach(trans -> {
                    waitOutBlock();
                    if (!super.verify(trans, utxoBundle.getOwner())) {
                        throw new RuntimeException("交易验证失败");
                    }
                });
                orderQueue.add(utxoBundle);
            }
            return verifyTimes.stream().reduce(Long::sum).orElse(0L);
        } finally {
            processing = false;
        }
        
    }
}
