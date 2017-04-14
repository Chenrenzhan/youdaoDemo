package test.com.youdao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import test.com.youdao.R;

/**
 * Created by DW on 2017/4/12.
 */
public class MineFragment extends BaseFragment {
    private Context mContext;
    
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_mine_fragment_alyout, container, false);
        return mRootView;
    }
}
