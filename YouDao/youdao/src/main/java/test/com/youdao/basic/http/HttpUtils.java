package test.com.youdao.basic.http;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DW on 2017/4/28.
 */
public class HttpUtils {

    /**
     * URL检查<br>
     * <br>
     * @param url     要检查的字符串<br>
     * @return boolean   返回检查结果<br>
     */
    public static boolean isValidUrl(String url) {
        if(TextUtils.isEmpty(url)){
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("^(http|https|ftp)://([a-zA-Z0-9//.//-]+(//:[a-zA-")
                .append("Z0-9//.&%//$//-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{")
                .append("2}|[1-9]{1}[0-9]{1}|[1-9])//.(25[0-5]|2[0-4][0-9]|[0-1]{1}")
                .append("[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)//.(25[0-5]|2[0-4][0-9]|")
                .append("[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)//.(25[0-5]|2[0-")
                .append("4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0")
                .append("-9//-]+//.)*[a-zA-Z0-9//-]+//.[a-zA-Z]{2,4})(//:[0-9]+)?(/")
                .append("[^/][a-zA-Z0-9//.//,//?//'///////+&%//$//=~_//-@]*)*$");

        String regEx = sb.toString();
        regEx = "^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?$";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(url);
        return matcher.matches();
    }
    
}
