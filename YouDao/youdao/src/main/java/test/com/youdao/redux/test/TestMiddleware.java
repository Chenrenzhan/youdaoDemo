package test.com.youdao.redux.test;

import io.reactivex.Observable;
import test.com.youdao.basic.redux.Action;
import test.com.youdao.basic.redux.Middleware;

/**
 * Created by DW on 2017/5/4.
 */
public class TestMiddleware implements Middleware {
    @Override
    public boolean canHandlerAction(Action action) {
        return false;
    }

    @Override
    public Observable<? extends Action> process(Action action) {
        return null;
    }
}
