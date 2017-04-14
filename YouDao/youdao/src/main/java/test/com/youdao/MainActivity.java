package test.com.youdao;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
    
    private MyViewPager mViewPager;
    
    private ImageView mTabIcDict;
    private TextView mTabLabelDict;
    private ImageView mTabIcTran;
    private TextView mTabLabelTran;
    private ImageView mTabIcCourse;
    private TextView mTabLabelCourse;
    private ImageView mTabIcExploer;
    private TextView mTabLabelExploer;
    private ImageView mTabIcMine;
    private TextView mTabLabelMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youdao_home);

        initView();
    }
    
    private void initView(){
        mViewPager = (MyViewPager) findViewById(R.id.view_pager);
        mViewPager.setScroll(false);
        mViewPager.setAdapter(new HomeFragmentAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        
        
        findViewById(R.id.home_dictionary).setOnClickListener(new OnTabClickListener(StaticConstant.TAB_INDEX_DICT));
        findViewById(R.id.home_translate).setOnClickListener(new OnTabClickListener(StaticConstant.TAB_INDEX_TRAN));
        findViewById(R.id.home_course).setOnClickListener(new OnTabClickListener(StaticConstant.TAB_INDEX_COURSE));
        findViewById(R.id.home_explore).setOnClickListener(new OnTabClickListener(StaticConstant.TAB_INDEX_EXPLORE));
        findViewById(R.id.home_mine).setOnClickListener(new OnTabClickListener(StaticConstant.TAB_INDEX_MINE));

        mTabIcDict = (ImageView) findViewById(R.id.ic_dictionary);
        mTabLabelDict = (TextView)findViewById(R.id.label_dictionary);
        mTabIcTran = (ImageView) findViewById(R.id.ic_translate);
        mTabLabelTran = (TextView)findViewById(R.id.label_translate);
        mTabIcCourse = (ImageView) findViewById(R.id.ic_course);
        mTabLabelCourse = (TextView)findViewById(R.id.label_course);
        mTabIcExploer = (ImageView) findViewById(R.id.ic_explore);
        mTabLabelExploer = (TextView)findViewById(R.id.label_explore);
        mTabIcMine = (ImageView) findViewById(R.id.ic_mine);
        mTabLabelMine = (TextView)findViewById(R.id.label_mine);

        mOnPageChangeListener.onPageSelected(StaticConstant.TAB_INDEX_DICT);
//        mViewPager.setCurrentItem(StaticConstant.TAB_INDEX_DICT);
    }
    
    private class OnTabClickListener implements View.OnClickListener {
        private int mIndex = 0;
        public OnTabClickListener(int index){
            mIndex = index;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(mIndex);
            resetTabState();
            if(mIndex == StaticConstant.TAB_INDEX_DICT){
                mTabIcDict.setBackgroundResource(R.drawable.ic_dictionary_use);
                mTabLabelDict.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(mIndex == StaticConstant.TAB_INDEX_TRAN){
                mTabIcTran.setBackgroundResource(R.drawable.ic_translate_use);
                mTabLabelTran.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(mIndex == StaticConstant.TAB_INDEX_COURSE){
                mTabIcCourse.setBackgroundResource(R.drawable.ic_course_use);
                mTabLabelCourse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(mIndex == StaticConstant.TAB_INDEX_EXPLORE){
                mTabIcExploer.setBackgroundResource(R.drawable.ic_explore_use);
                mTabLabelExploer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(mIndex == StaticConstant.TAB_INDEX_MINE){
                mTabIcMine.setBackgroundResource(R.drawable.ic_mine_use);
                mTabLabelMine.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            }
        }
    }
    
    private void resetTabState(){
        mTabIcDict.setBackgroundResource(R.drawable.ic_dictionary_nonuse);
        mTabLabelDict.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_normal_color));
        mTabIcTran.setBackgroundResource(R.drawable.ic_translate_nonuse);
        mTabLabelTran.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_normal_color));
        mTabIcCourse.setBackgroundResource(R.drawable.ic_course_nonuse);
        mTabLabelCourse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_normal_color));
        mTabIcExploer.setBackgroundResource(R.drawable.ic_explore_nonuse);
        mTabLabelExploer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_normal_color));
        mTabIcMine.setBackgroundResource(R.drawable.ic_mine_nonuse);
        mTabLabelMine.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_normal_color));
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("chenrenzhan", "position = " + position);
            resetTabState();
            if(position == StaticConstant.TAB_INDEX_DICT){
                mTabIcDict.setBackgroundResource(R.drawable.ic_dictionary_use);
                mTabLabelDict.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(position == StaticConstant.TAB_INDEX_TRAN){
                mTabIcTran.setBackgroundResource(R.drawable.ic_translate_use);
                mTabLabelTran.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(position == StaticConstant.TAB_INDEX_COURSE){
                mTabIcCourse.setBackgroundResource(R.drawable.ic_course_use);
                mTabLabelCourse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(position == StaticConstant.TAB_INDEX_EXPLORE){
                mTabIcExploer.setBackgroundResource(R.drawable.ic_explore_use);
                mTabLabelExploer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            } else if(position == StaticConstant.TAB_INDEX_MINE){
                mTabIcMine.setBackgroundResource(R.drawable.ic_mine_use);
                mTabLabelMine.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.home_tab_selected_color));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
