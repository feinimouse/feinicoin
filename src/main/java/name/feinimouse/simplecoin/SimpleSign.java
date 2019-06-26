package name.feinimouse.simplecoin;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;

import name.feinimouse.feinicoin.account.Sign;

public class SimpleSign implements Sign {
    private Map<String, byte[]> signMap;

    public SimpleSign() {
        this.signMap = new HashMap<>();
    }

    public void setSign(String name, byte[] sign) {
        signMap.put(name, sign);
    }

    public void setSign(String name, String sign) {
        signMap.put(name, Hex.decode(sign));
    }

    public String toString(String name) {
        return Hex.toHexString(getByte(name));
    }

    @Override
    public byte[] getByte(String name) throws NullPointerException {
        return signMap.get(name);
    }
}