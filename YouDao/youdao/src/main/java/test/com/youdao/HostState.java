package test.com.youdao;


import android.support.annotation.NonNull;

import test.com.youdao.basic.redux.store.State;

/**
 * Created by ruoshili on 2/16/2017.
 */
public final class HostState extends State {
    private static final String TAG = "HostState";
    // TODO: 定义宿主的状态
    // 状态类型要避免出现对象的嵌套和循环引用



    private HostState(Builder builder) {
        super(builder);
    }


    public static final class Builder extends State.Builder<HostState> {
        public Builder() {
            this(null);
        }

        public Builder(final HostState originalState) {
            if (originalState == null) {
                return;
            }
        }

        @NonNull
        @Override
        public HostState build() {
            return new HostState(this);
        }

    }
}
