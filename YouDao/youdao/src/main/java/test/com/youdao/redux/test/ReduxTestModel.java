package test.com.youdao.redux.test;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import test.com.youdao.basic.redux.store.State;

/**
 * Created by DW on 2017/5/4.
 */
public class ReduxTestModel extends State{
    private final int uid;
    private final String name;
    
    protected ReduxTestModel(Builder builder) {
        super(builder);
        uid = builder.uid;
        name = builder.name;
    }

    @Override
    public String toString() {
        return "ReduxTestModel{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder extends State.Builder<ReduxTestModel>{
        private int uid;
        private String name;
        
        public Builder(){}
        
        public Builder(ReduxTestModel original){
            this.uid = original.uid;
            this.name = original.name;
        }
        
        @NonNull
        @Override
        public ReduxTestModel build() {
            return new ReduxTestModel(this);
        }
        
        public Builder setUid(int uid){
            this.uid = uid;
            return this;
        }
        
        public Builder setName(String name){
            this.name = name;
            return this;
        }
    }
    
    
}
