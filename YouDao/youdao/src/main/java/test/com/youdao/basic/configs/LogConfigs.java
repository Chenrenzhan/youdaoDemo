package test.com.youdao.basic.configs;

/**
 * Created by DW on 2017/4/14.
 */
public interface LogConfigs {
    String LOG_EXT = ".txt";
    String LOG_TAG = "yymobile_log_files";
    String LOG_RECORDS = "yy_log_records";
    String OLD_LOGS = "logs.txt";
    String UNCAUGHT_EXCEPTIONS_LOGS = "uncaught_exception.txt";
    String LOG_DESCRIPTION = "log_description.txt";
    float AVERAGE_LOG_ZIP_COMPRESSION_RATIO = 0.15f;//ZIP方式在手Y的压log的平均压缩率，用于收集日志时，估算日志压缩后大小
    String LOG_DATE_FORMAT_STR = "yyyy_MM_dd_HH";
    String LOG_DATE_FORMAT_MINUTE_STR = "yyyy_MM_dd_HH_mm";
    int MAX_FILE_SIZE = 101;//日志目录下最大文件长度为101M（超过则删除该文件，比写入的100M大1M保证不会误删）
    int SDK_LOG_FILE_LIMIT_ZIP_SIZE = 5;
}
