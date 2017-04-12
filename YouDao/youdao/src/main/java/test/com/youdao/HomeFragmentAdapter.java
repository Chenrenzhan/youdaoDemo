package test.com.youdao;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import test.com.youdao.fragment.CourseFragment;
import test.com.youdao.fragment.DictFragment;
import test.com.youdao.fragment.ExploerFragment;
import test.com.youdao.fragment.MineFragment;
import test.com.youdao.fragment.TranFragment;

/**
 * Created by DW on 2017/4/12.
 */
public class HomeFragmentAdapter extends FragmentPagerAdapter {
    public HomeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == StaticConstant.TAB_INDEX_DICT){
            return new DictFragment();
        } else if(position == StaticConstant.TAB_INDEX_TRAN){
            return new TranFragment();
        } else if(position == StaticConstant.TAB_INDEX_COURSE){
            return new CourseFragment();
        } else if(position == StaticConstant.TAB_INDEX_EXPLORE){
            return new ExploerFragment();
        } else if(position == StaticConstant.TAB_INDEX_MINE){
            return new MineFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return StaticConstant.TAB_COUNT;
    }
}
