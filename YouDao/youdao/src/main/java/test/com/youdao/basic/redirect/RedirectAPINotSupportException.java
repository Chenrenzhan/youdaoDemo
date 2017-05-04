package test.com.youdao.basic.redirect;

import android.net.Uri;

public class RedirectAPINotSupportException extends Exception{
    private String uriString;
    public RedirectAPINotSupportException(Uri uri) {
        this(genDetailMessage(uri));
        if(uri!=null){
            uriString = uri.toString();
        }
    }

    public RedirectAPINotSupportException(String detailMessage) {
        super(detailMessage);
    }

    public String getUriString() {
        return uriString;
    }

    private static String genDetailMessage(Uri uri){
        if(uri!=null){
            return "API:"+uri.toString()+" not support.";
        }else{
            return "API: null uri.";
        }
    }
}