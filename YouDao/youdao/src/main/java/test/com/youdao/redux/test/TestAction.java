package test.com.youdao.redux.test;

import test.com.youdao.basic.redux.Action;

/**
 * Created by DW on 2017/5/4.
 */
public class TestAction implements Action {
    private ReduxTestModel mTestModel;
    
    public TestAction(ReduxTestModel testModel){
        mTestModel = testModel;
    }
    
    @Override
    public String getActionTypeName() {
        return TestAction.class.getName();
    }
    
    public void setTestModel(ReduxTestModel testModel){
        mTestModel = testModel;
    }
    
    public ReduxTestModel getTestModel(){
        return mTestModel;
    }

    @Override
    public String toString() {
        return "TestAction{" +
                "mTestModel=" + mTestModel +
                '}';
    }
}
