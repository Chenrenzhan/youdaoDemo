package test.com.youdao.basic.http;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import test.com.youdao.basic.log.MLog;

/**
 * Created by DW on 2017/4/28.
 */
public class RequestParam {
    private final static String TAG = "RequestParam";
    public final static int GET = 1; // get 方式请求
    public final static int POST = 2; // post 方式请求
    public final static int UPLOAD = 3; // 上传
    public final static int DOWNLOAD = 4; // 下载
    private int method = GET; // 请求方式
    private String url; // 请求链接 http://www.runoob.com/html/html-tutorial.html?test1=1&test2=2
    private String baseUrl; // 主机名称， url.getProtocol() + url.getHost(), http://www.runoob.com
    private String pathUrl; // 路径 ， url.getPath(), html/html-tutorial.html
    private Map params = new HashMap(); // <test1, 1> <test2, 2>
    
    public Builder newBuilder(){
        return new Builder()
                .method(this.method)
                .url(this.url)
                .baseUrl(this.baseUrl)
                .pathUrl(this.pathUrl)
                .params(this.params);
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public Map getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "RequestParam{" +
                "method=" + method +
                ", url='" + url + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", pathUrl='" + pathUrl + '\'' +
                ", params=" + params +
                '}';
    }

    public static class Builder{
        RequestParam requestParam;
        
        public Builder(){
            requestParam = new RequestParam();
        }

        public Builder method(int method){
            requestParam.method = method;
            return this;
        }
        
        public Builder url(String url){
            requestParam.url = url;
            parseUrl();
            return this;
        }

        public Builder baseUrl(String baseUrl){
            requestParam.baseUrl = baseUrl;
            return this;
        }

        public Builder pathUrl(String subUrl){
            requestParam.pathUrl = subUrl;
            return this;
        }
        
        public <T> Builder params(Map<String, T> params){
            if(requestParam.params == null){
                requestParam.params = new HashMap<String, T>();
            }
            requestParam.params.putAll(params);
            return this;
        }
        
        public <T> Builder addParam(String key, T value){
            if(requestParam.params == null){
                requestParam.params = new HashMap<String, T>();
            }
            requestParam.params.put(key, value);
            return this;
        }
        
        public RequestParam build(){
            return requestParam;
        }

        private void parseUrl() {
            if(!HttpUtils.isValidUrl(requestParam.url)){
                MLog.error(TAG, "invalid base url: " + requestParam.url);
                return;
            }
            requestParam.url = requestParam.url.trim();
            String [] urlSplit = requestParam.url.split("[?]");
            if(urlSplit.length <= 0){
                MLog.error(TAG, "invalid base url: " + requestParam.url);
                return;
            }
            URL urlTmp = null;
            try {
                urlTmp = new URL(urlSplit[0]);
            } catch (MalformedURLException e) {
                MLog.error(TAG, "invalid base url: " + requestParam.url + " , MalformedURLException : " + e);
                return;
            }
            requestParam.baseUrl = urlTmp.getProtocol() + "://" + urlTmp.getHost() + "/";
            requestParam.pathUrl = urlTmp.getPath();
            if(requestParam.pathUrl.charAt(0) == '/'){
                requestParam.pathUrl = requestParam.pathUrl.substring(1);
            }
            if(urlSplit.length > 1){
                if(requestParam.params == null){
                    requestParam.params = new HashMap<String, String>();
                }
                requestParam.params.putAll(parseParam(urlSplit[1]));
            }
        }

        private Map<String, String> parseParam(String urlParam){
            Map<String, String> mapRequest = new HashMap<String, String>();
            if(TextUtils.isEmpty(urlParam)){
                return mapRequest;
            }
            String[] arrSplit=urlParam.split("[&]");
            for (String strSplit : arrSplit) {
                String[] arrSplitEqual = strSplit.split("[=]");
                if (arrSplitEqual.length > 1) {
                    mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
                } else if(!TextUtils.isEmpty(arrSplitEqual[0])) {
                    mapRequest.put(arrSplitEqual[0], "");

                }
            }
            return mapRequest;
        }
    }
}
