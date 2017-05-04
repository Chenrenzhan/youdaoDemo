package test.com.youdao.basic.utils;

/**
 * Created by DW on 2017/4/13.
 */
public abstract class SingleTonUtils<T> {
    private T mInstance;
    private final static Object syncLock = new Object();
    
    public abstract T create();
    
    public T get(){
        if(mInstance == null){
            synchronized (syncLock){
                mInstance = create();
            }
        }
        return mInstance;
    }
}
