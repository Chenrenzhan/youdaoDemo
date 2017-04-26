package test.com.youdao.model;

import java.util.Map;

/**
 * Created by DW on 2017/4/13.
 */
public class ActionModel {
    public String action = ""; // action 必须全局唯一性，建议使用：包名.类名.XXX，比如 test.com.youdao.model.ActionModel.MY_ACTION
    public Object object;
    public Map<String, Object> extend;
}
