package test.com.youdao.basic.rxbus;

import java.util.Map;

/**
 * Created by DW on 2017/4/13.
 * 为避免 RxBus 每一次都要写一个类，定义一个通用的用 action 字符串来区分同一个 RxBusAction 实例对象的事件
 */
public class RxBusAction {
    public String action = ""; // action 必须全局唯一性，建议使用：包名.类名.XXX，比如 test.com.youdao.basic.rxbus.RxBusAction.MY_ACTION
    public Object object;
    public Map<String, Object> extend;
}
