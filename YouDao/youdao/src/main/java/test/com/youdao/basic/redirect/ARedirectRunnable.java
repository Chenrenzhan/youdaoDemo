package test.com.youdao.basic.redirect;

/**
 * Created by DW on 2017/5/4.
 */
public abstract class ARedirectRunnable<T extends IRedirectParam> implements Runnable {
    private T mParam;

    public void setParam(T param){
        mParam = param;
    }

    public T getParam(){
        return mParam;
    }
}
