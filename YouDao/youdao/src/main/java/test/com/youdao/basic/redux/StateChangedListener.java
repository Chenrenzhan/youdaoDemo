package test.com.youdao.basic.redux;

/**
 * Created by ruoshili on 2/16/2017.
 */

public interface StateChangedListener<TState> {
    void onStateChanged(StateChangedEventArgs<TState> eventArgs);
}
