package test.com.youdao.basic.crash;

import android.app.Application;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import test.com.youdao.MainActivity;
import test.com.youdao.basic.utils.SingleTonUtils;
import test.com.youdao.basic.log.LogToES;
import test.com.youdao.basic.log.MLog;

/**
 * 崩溃捕获处理，可以捕获全局的未处理异常
 * 
 * @author chengaochang
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Application mApplication;
    private static CrashHandler mCrashHandler = new SingleTonUtils<CrashHandler>(){
        @Override
        public CrashHandler create() {
            return new CrashHandler();
        }
    }.get();

    private CrashHandler(){
    }
    
    public static CrashHandler getInstance(){
        return mCrashHandler;
    }
    
    private static void crateInstance(){
        mCrashHandler = new CrashHandler();
    }
    
    public void init(Application app){
        mApplication = app;
    }


    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {
            //写到UNCAUGHT_EXCEPTIONS_LOGNAME
            String crashData = collectStackTrace(ex);
            writeTraceToLog(crashData,ex);
            MLog.flush();
            Thread.sleep(1000);//等待1秒，等崩溃日志线程跑一会，写入日志到文件
        } catch (Exception e) {
            MLog.error(this, ex);
        }
//        restartApp();
    }
    
    public void restartApp(){
        // 发生异常后，重新启动应用  
        Intent intent = new Intent(mApplication, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // application context 必须添加 FLAG_ACTIVITY_NEW_TASK 标志
        mApplication.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public  String collectStackTrace(Throwable th) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        Throwable cause = th;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            break;// 只取第一层
        }
        String stackTrace = result.toString();
        printWriter.close();
        return stackTrace.trim();
    }

    private  void writeTraceToLog(String traces,Throwable ex) {
        try {
            MLog.error(this, traces);
            LogToES.writeLogToFile(LogToES.getLogPath(), CrashConfig.UNCAUGHT_EXCEPTIONS_LOGNAME, traces,
                    true, System.currentTimeMillis());

        } catch (Exception e) {
            MLog.error(this, e);
        }
    }
}
