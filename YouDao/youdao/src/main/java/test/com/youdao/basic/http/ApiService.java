package test.com.youdao.basic.http;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    public static final String Base_URL = "http://ip.taobao.com/";

    /**
     * 普通写法
     */
    @GET("service/getIpInfo.php/")
    Call<ResponseBody> getData(@Query("ip") String ip);

    @GET("{url}")
    Call<ResponseBody> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);


    @POST("{url}")
    <T> Call<ResponseBody> executePost(
            @Path("url") String url,
            @QueryMap Map<String, T> maps);

    @Multipart
    @POST("{url}")
    Call<ResponseBody> upLoadFile(
            @Path("url") String url,
            @Part RequestBody avatar);


    @POST("{url}")
    Call<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

}