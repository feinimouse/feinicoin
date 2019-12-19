package name.feinimouse.feinicoinplus.core.base;

import org.json.JSONObject;

public class MerkelObj extends HashObj {
    
    private HashObj[] arr;
    private String[] hashTree;
    
    public MerkelObj(HashObj[] arr, String[] hashTree) {
        this.hash = hashTree[0];
        this.arr = arr;
        this.hashTree = hashTree;
    }
    
    @Override
    public String summary() {
        return JsonAble.genJson(arr).toString();
    }
    
    @Override
    public HashObj[] getObject() {
        return arr;
    }

    @Override
    public JSONObject json() {
        return new JSONObject().put("obj", JsonAble.genJson(arr)).put("hash", hash);
    }

    public String[] hashTree() {
        return hashTree;
    }
    
}