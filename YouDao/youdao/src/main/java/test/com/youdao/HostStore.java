package test.com.youdao;

import test.com.youdao.basic.redux.store.AbstractStore;

/**
 * Created by ruoshili on 2/16/2017.
 * 宿主的状态存储
 */
public class HostStore extends AbstractStore<HostState> {
    public final static HostStore INSTANCE = new HostStore();

    private HostStore() {
        init(new HostState.Builder().build());
    }
}
