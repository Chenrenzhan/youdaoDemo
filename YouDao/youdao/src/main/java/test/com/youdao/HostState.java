package test.com.youdao;


import android.support.annotation.NonNull;

import test.com.youdao.basic.redux.store.State;
import test.com.youdao.redux.test.ReduxTestModel;

/**
 * Created by ruoshili on 2/16/2017.
 */
public final class HostState extends State {
    private static final String TAG = "HostState";
    // TODO: 定义宿主的状态
    // 状态类型要避免出现对象的嵌套和循环引用

    private final ReduxTestModel mTestModel;

    private HostState(Builder builder) {
        super(builder);
        mTestModel = builder.mTestModel;
    }

    public ReduxTestModel getTestModel() {
        return mTestModel;
    }

    public static final class Builder extends State.Builder<HostState> {
        private ReduxTestModel mTestModel;
        public Builder() {
            this(null);
        }

        public Builder(final HostState originalState) {
            if (originalState == null) {
                return;
            }
        }

        public Builder setTestModel(ReduxTestModel testModel) {
            this.mTestModel = testModel;
            return this;
        }

        @NonNull
        @Override
        public HostState build() {
            return new HostState(this);
        }

    }
}
