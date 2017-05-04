package test.com.youdao.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import test.com.youdao.HostState;
import test.com.youdao.HostStore;
import test.com.youdao.R;
import test.com.youdao.basic.http.OnHttpErrorListener;
import test.com.youdao.basic.http.RequestParam;
import test.com.youdao.basic.http.RxRetrofit;
import test.com.youdao.basic.redirect.RedirectAPINotSupportException;
import test.com.youdao.basic.redirect.RedirectHandler;
import test.com.youdao.basic.redirect.RedirectParam;
import test.com.youdao.basic.redux.StateChangedEventArgs;
import test.com.youdao.basic.rxbus.RxBus;
import test.com.youdao.basic.rxbus.RxBusAction;
import test.com.youdao.basic.log.MLog;
import test.com.youdao.redux.test.ReduxTestModel;
import test.com.youdao.redux.test.TestAction;

/**
 * Created by DW on 2017/4/12.
 */
public class DictFragment extends BaseFragment {
    private Context mContext;
    
    private View mRootView;
    
    int count = 0;

    Observable<RxBusAction> observable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_dict_fragment_alyout, container, false);

        mRootView.findViewById(R.id.post_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RxBusAction action = new RxBusAction();
                action.action = "test.com.youdao.fragment.DictFragment.TEST";
                action.object = "RxBus test " + count++;
//                RxBus.getDefault().postSticky(action);
                RxBus.getDefault().postAction(action);
            }
        });
        
        mRootView.findViewById(R.id.register_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 粘性事件
//                RxBus.getDefault().registerSticky(RxBusAction.class, DictFragment.this)
//                        .filter(new Predicate<RxBusAction>() {
//                            @Override
//                            public boolean test(@NonNull RxBusAction actionModel) throws Exception {
//                                return "test.com.youdao.fragment.DictFragment.TEST".equals(actionModel.action);
//                            }
//                        })
//                        .subscribe(new Consumer<RxBusAction>() {
//                            @Override
//                            public void accept(@NonNull RxBusAction actionModel) throws Exception {
////                                Toast.makeText(getContext(), actionModel.object.toString(), Toast.LENGTH_SHORT).show();
//                                MLog.debug("chenrenzhan", actionModel.action + "  registerSticky");
//                            }
//                        });
//
//                RxBus.getDefault().register(RxBusAction.class, DictFragment.this)
//                        .filter(new Predicate<RxBusAction>() {
//                            @Override
//                            public boolean test(@NonNull RxBusAction actionModel) throws Exception {
//                                return "test.com.youdao.fragment.DictFragment.TEST".equals(actionModel.action);
//                            }
//                        })
//                        .subscribe(new Consumer<RxBusAction>() {
//                            @Override
//                            public void accept(@NonNull RxBusAction actionModel) throws Exception {
////                                Toast.makeText(getContext(), actionModel.object.toString(), Toast.LENGTH_SHORT).show();
//                                MLog.debug("chenrenzhan", actionModel.action + "  register");
//                            }
//                        });

                RxBus.getDefault().registerAction("test.com.youdao.fragment.DictFragment.TEST")
                        .subscribe(new Consumer<RxBusAction>() {
                            @Override
                            public void accept(@NonNull RxBusAction rxBusAction) throws Exception {
                                Toast.makeText(getContext(), rxBusAction.object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        
        mRootView.findViewById(R.id.redirect_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 自定义 URI 重定向测试
                try {
                    String uri = RedirectHandler.URI_HOST + "RedirectTest/redirect/test/param1/param2?param=2";
                    RedirectParam param = new RedirectParam();
                    param.srActivity = new SoftReference<Activity>(getActivity());
                    param.uri = Uri.parse(uri);
                    RedirectHandler.getInstance().handleUriString(uri, param);
                } catch (RedirectAPINotSupportException e) {
                    e.printStackTrace();
                }
            }
        });
        
        mRootView.findViewById(R.id.retrofit_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // https://api.github.com/users/list/
                // http://data.3g.yy.com/plugin/infos
                RxRetrofit.request(new RequestParam.Builder().method(RequestParam.GET).url("https://api.github.com/users/list/").build(),
                        new OnHttpErrorListener() {
                            @Override
                            public void onError(Throwable throwable) {
                                MLog.error("chenrenzhan", " doOnError : " + throwable);
                            }
                        })
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(@NonNull ResponseBody responseBody) throws Exception {
                                MLog.error("chenrenzhan", " responseBody : " + responseBody.string());
                            }
                        });
            }
        });

        testRedux();
        
        return mRootView;
    }
    
    private void testRedux(){
        HostStore.INSTANCE.getObservable()
                .filter(new Predicate<StateChangedEventArgs<HostState>>() {
                    @Override
                    public boolean test(@NonNull StateChangedEventArgs<HostState> eventArgs) throws Exception {
                        return TestAction.class.equals(eventArgs.action.getClass());
                    }
                })
                .subscribe(new Consumer<StateChangedEventArgs<HostState>>() {
                    @Override
                    public void accept(@NonNull StateChangedEventArgs<HostState> eventArgs) throws Exception {
                        TestAction action = (TestAction) eventArgs.action;
                        ReduxTestModel stateTestModel = HostStore.INSTANCE.getState().getTestModel(); // 
                        ReduxTestModel eventTestModel = eventArgs.state.getTestModel();
                        MLog.debug("chenrenzhan", " action --> " + action + " \n stateTestModel --> " + stateTestModel + " \n eventTestModel --> " + eventTestModel);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        
                    }
                });
        mRootView.findViewById(R.id.redux_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReduxTestModel testModel = new ReduxTestModel.Builder().setUid(count++).setName("chenrenzhan").build();
                TestAction action = new TestAction(testModel);
                HostStore.INSTANCE.dispatch(action);
            }
        });
    }
}
