package test.com.youdao.redux.test;

import android.support.annotation.NonNull;

import test.com.youdao.HostState;
import test.com.youdao.basic.redux.Reducer;

/**
 * Created by DW on 2017/5/4.
 */
public class ReduxTestRedecer implements Reducer<HostState, TestAction> {
    @NonNull
    @Override
    public Class<TestAction> getActionClass() {
        return TestAction.class;
    }

    @NonNull
    @Override
    public HostState reduce(TestAction action, HostState originalState) {
        // 修改原有在 HostState 保存的数据
        HostState.Builder builder = new HostState.Builder(originalState);
        builder.setTestModel(action.getTestModel()); // 直接替换新的 TestModel
        return builder.build();
    }

}
