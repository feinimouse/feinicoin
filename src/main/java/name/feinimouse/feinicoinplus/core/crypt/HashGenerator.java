package name.feinimouse.feinicoinplus.core.crypt;

import name.feinimouse.feinicoinplus.core.data.BlockObj;
import name.feinimouse.feinicoinplus.core.data.HashBlockObj;
import name.feinimouse.feinicoinplus.core.data.Packer;
import name.feinimouse.feinicoinplus.core.data.PackerArr;

public interface HashGenerator {
    String hash(String content);

    Packer hash(BlockObj blockObj);
    
    <T extends HashBlockObj> T hash(T hashBlockObj);
    
    Packer hash(Packer packer);

    PackerArr hash(BlockObj[] objArr, Class<?> aClass);

    PackerArr hash(Packer[] objArr, Class<?> aClass);
}
