package name.feinimouse.feinicoinplus.base.crypt;

import de.greenrobot.common.hash.Murmur3A;
import de.greenrobot.common.hash.Murmur3F;
import name.feinimouse.feinicoinplus.core.crypt.HashGenerator;
import name.feinimouse.feinicoinplus.core.data.BlockObj;
import name.feinimouse.feinicoinplus.core.data.HashBlockObj;
import name.feinimouse.feinicoinplus.core.data.Packer;
import name.feinimouse.feinicoinplus.core.data.PackerArr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.zip.Checksum;

@Component("hashGenerator")
public class MurmurHashGen implements HashGenerator {

    private Checksum murmur;

    @Autowired
    public MurmurHashGen(@Value("${HASH_SEED}") int seed) {
        this(seed, true);
    }
    
    public MurmurHashGen(int seed, boolean isLong) {
        if (isLong) {
            murmur = new Murmur3F(seed);
        } else {
            murmur = new Murmur3A(seed);
        }
    }

    @Override
    public String hash(String content) {
        murmur.reset();
        murmur.update(content.getBytes(StandardCharsets.UTF_8));
        return String.valueOf(murmur.getValue());
    }

    @Override
    public Packer hash(BlockObj blockObj) {
        Packer packer = new Packer(blockObj);
        packer.setHash(hash(blockObj.genSummary()));
        return packer;
    }

    @Override
    public <T extends HashBlockObj> T hash(T hashBlockObj) {
        String hash = hash(hashBlockObj.genSummary());
        hashBlockObj.setHash(hash);
        return hashBlockObj;
    }

    @Override
    public Packer hash(Packer packer) {
        String summary = ((BlockObj) packer.obj()).genSummary();
        packer.setHash(hash(summary));
        return packer;
    }

    @Override
    public PackerArr hash(BlockObj[] objArr, Class<?> aClass) {
        int length = objArr.length;
        if (length <= 0 || !objArr[0].getClass().equals(aClass)) {
            return new PackerArr("0000000000000000", new Packer[]{}, aClass);
        }
        if (length == 1) {
            Packer packer = hash(objArr[0]);
            return new PackerArr(packer.getHash(), new Packer[]{packer}, aClass);
        }
        Packer[] packers = new Packer[length];
        String[] hashTree = new String[length * 2 - 1];
        for (int i = 0; i < length; i++) {
            Packer packer = hash(objArr[i]);
            packers[i] = packer;
            hashTree[i + length - 1] = packer.getHash();
        }
        genMerkelHash(0, hashTree);
        return new PackerArr(hashTree[0], packers, aClass);
    }

    public PackerArr hash(Packer[] objArr, Class<?> aClass) {
        int length = objArr.length;
        if (length <= 0 || !objArr[0].objClass().equals(aClass)) {
            return new PackerArr("0000000000000000", objArr, aClass);
        }
        if (length == 1) {
            return new PackerArr(objArr[0].getHash(), objArr, aClass);
        }
        String[] hashTree = new String[length * 2 - 1];
        for (int i = 0; i < length; i++) {
            hashTree[i + length - 1] = objArr[i].getHash();
        }
        genMerkelHash(0, hashTree);
        return new PackerArr(hashTree[0], objArr, aClass);
    }

    private String genMerkelHash(int root, String[] hashTree) {
        if (2 * root + 1 >= hashTree.length) {
            return hashTree[root];
        }
        return hashTree[root] = hash(genMerkelHash(2 * root + 1, hashTree)
            + genMerkelHash(2 * root + 2, hashTree));
    }

}
