package test.com.youdao.basic.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import test.com.youdao.MyApplication;
import test.com.youdao.basic.configs.BasicConfigs;
import test.com.youdao.basic.log.MLog;
import test.com.youdao.basic.pref.CommonPref;

/**
 * Created by DW on 2017/4/13.
 * 设备工具类，比如网络判断等跟设备相关的操作
 */
public class DeviceUtils {
    private final static String TAG = "DeviceUtils";
    private final static String IS_DEBUG = "is_debugMode";
    private static boolean isDebuggable;
    private SoftReference<File> mLogDir;
    private SoftReference<File> mRoot;
    private SoftReference<File> mConfigDir;

    private static SoftReference<BroadcastReceiver> mExternalStorageReceiver;
    private static boolean mExternalStorageAvailable = false;
    private static boolean mExternalStorageWriteable = false;
    private static volatile boolean mExternalStorageChecked = false;

    public static boolean isDebuggable(){
        if (CommonPref.instance().contain(IS_DEBUG)) {
            isDebuggable = CommonPref.instance().getBoolean(IS_DEBUG, false);
            Observable.timer(5000, TimeUnit.MILLISECONDS, Schedulers.newThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            updateDebuggableFlag();
                        }
                    });
        } else {
            updateDebuggableFlag();
        }
        return isDebuggable;
    }

    private static void updateDebuggableFlag() {
        ApplicationInfo appInfo = null;
        PackageManager packMgmr = MyApplication.getApplication().getPackageManager();
        try {
            appInfo = packMgmr.getApplicationInfo(MyApplication.getApplication().getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            MLog.error(TAG, e);
        }
        if (appInfo != null) {
            setDebuggable((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0);
        }
    }

    private static void setDebuggable() {
        if (CommonPref.instance().contain(IS_DEBUG)) {
            isDebuggable = CommonPref.instance().getBoolean(IS_DEBUG, false);
            
            Observable.timer(5000, TimeUnit.MILLISECONDS, Schedulers.newThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            updateDebuggableFlag();
                        }
                    });
        } else {
            updateDebuggableFlag();
        }
    }

    public static void setDebuggable(boolean debuggable){
        isDebuggable = debuggable;
        CommonPref.instance().putBoolean(IS_DEBUG, isDebuggable);
    }
    
    private static File getPathDir(SoftReference<File> file, String dir){
        if(file == null || file.get() == null){
            try{
                File configDir = getDirFile(MyApplication.getApplication(), dir); 
                if (!configDir.exists()){
                    if(!configDir.mkdirs()){
                        MLog.error(TAG, "Can't create config dir " + configDir);
                        return null;
                    }
                }
                file = new SoftReference<File>(configDir);
            } catch (Exception e){
                MLog.error(TAG, "Set config dir error", e);
                return null;
            }

        }
        return file.get();
    }

    public File getRootDir(){
        return getPathDir(mRoot, BasicConfigs.ROOT_DIR); 
    }

    public File getConfigDir(){
        return getPathDir(mConfigDir, BasicConfigs.CONFIGS_DIR);  
    }

    public File getLogDir(){
        return getPathDir(mLogDir, BasicConfigs.LOGS_DIR);  
    }

    public static boolean isExternalStorageAvailable() {
        startExternalState();
        return mExternalStorageAvailable;
    }

    public static boolean isExternalStorageWriteable() {
        startExternalState();
        return mExternalStorageWriteable;
    }

    public static void startExternalState() {
        if (!mExternalStorageChecked) {
            updateExternalStorageState();
            startWatchingExternalStorage();
            mExternalStorageChecked = true;
        }
    }

    public static void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public static void startWatchingExternalStorage() {
        if (MyApplication.getApplication() == null) {
            MLog.error(TAG, "mContext null when startWatchingExternalStorage");
            return;
        }
        if(mExternalStorageReceiver == null || mExternalStorageReceiver.get() == null){
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MLog.info("ExternalStorageReceiver", "Storage: " + intent.getData());
                    updateExternalStorageState();
                }
            };
            mExternalStorageReceiver = new SoftReference<BroadcastReceiver>(receiver);
        }
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        MyApplication.getApplication().registerReceiver(mExternalStorageReceiver.get(), filter);
    }

    public static void stopWatchingExternalStorage() {
        if (MyApplication.getApplication() == null) {
            MLog.error(TAG, "mContext null when stopWatchingExternalStorage");
            return;
        }
        MyApplication.getApplication().unregisterReceiver(mExternalStorageReceiver.get());
    }

    public static File getDirFile(Context context, String uniqueName) {
        final String cachePath = isExternalStorageAvailable() ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    private static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    private static File getExternalCacheDir(Context context) {
        return new File(Environment.getExternalStorageDirectory().getPath());
    }

    public static String getImei(Context c) {
        TelephonyManager manager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = manager.getDeviceId();
        if (!TextUtils.isEmpty(imei) && !imei.matches("0+") && !imei.equals("004999010640000")){
            return imei;
        }
        return "";
    }

    public static int getScreenWidth(Context context) {
        return getScreenSize(context, null).x;
    }

    public static int getScreenHeight(Context context) {
        return getScreenSize(context, null).y;
    }

    public static Point getScreenSize(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point ret = outSize == null ? new Point() : outSize;
        final Display defaultDisplay = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(ret);
        }
        else {
            Point point = new Point();
            defaultDisplay.getSize(point);
            ret.x = point.x;
            ret.y = point.y;
        }
        return ret;
    }

    public static float convertDpToPixel(float dp, Context context){

        try {
            if(context==null){
                return dp;
            }
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return px;
        }catch (Exception ex){
        }

        return -1;
    }

    public static float convertPixelsToDp(float px, Context context){
        try {

            if(context==null){
                return px;
            }
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = px / (metrics.densityDpi / 160f);
            return dp;

        }catch (Exception ex){

        }
        return -1;
    }

    /**
     * 返回屏幕的宽高比
     * @param context
     * @param outSize
     * @return
     */
    public static float getScreenWidthHeightRatio(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point ret = outSize == null ? new Point() : outSize;
        final Display defaultDisplay = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(ret);
        }
        else {
            ret.x = defaultDisplay.getWidth();
            ret.y = defaultDisplay.getHeight();
        }
        float xf = ret.x;
        float yf = ret.y;
        return xf/yf;
    }
}
