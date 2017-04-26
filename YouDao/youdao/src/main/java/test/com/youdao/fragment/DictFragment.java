package test.com.youdao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import test.com.youdao.R;
import test.com.youdao.basic.RxBus;
import test.com.youdao.model.ActionModel;

/**
 * Created by DW on 2017/4/12.
 */
public class DictFragment extends BaseFragment {
    private Context mContext;
    
    private View mRootView;
    
    int count = 0;

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
                ActionModel action = new ActionModel();
                action.action = "test.com.youdao.fragment.DictFragment.TEST";
                action.object = "RxBus test " + count++;
                RxBus.getDefault().postSticky(action);
            }
        });
        
        mRootView.findViewById(R.id.register_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().registerSticky(ActionModel.class, DictFragment.this)
                        .filter(new Predicate<ActionModel>() {
                            @Override
                            public boolean test(@NonNull ActionModel actionModel) throws Exception {
                                return "test.com.youdao.fragment.DictFragment.TEST".equals(actionModel.action);
                            }
                        })
                        .subscribe(new Consumer<ActionModel>() {
                            @Override
                            public void accept(@NonNull ActionModel actionModel) throws Exception {
                                Toast.makeText(getContext(), actionModel.object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        
        return mRootView;
    }
}
