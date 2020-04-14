package com.foo.udf;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author liuhuixu
 * @create 2020-04-01 15:22
 * 解析基础的明细字段的表(一对一)
 */
public class BaseFieldUDF extends UDF {
    public static void main(String[] args) {
        String line="1585497647294|{\"cm\":{\"ln\":\"-63.3\",\"sv\":\"V2.3.5\",\"os\":\"8.0.3\",\"g\":\"OT3BZ219@gmail.com\",\"mid\":\"981\",\"nw\":\"3G\",\"l\":\"es\",\"vc\":\"16\",\"hw\":\"640*960\",\"ar\":\"MX\",\"uid\":\"981\",\"t\":\"1585478111477\",\"la\":\"-1.7\",\"md\":\"Huawei-8\",\"vn\":\"1.2.8\",\"ba\":\"Huawei\",\"sr\":\"A\"},                     \"ap\":\"app\",\"et\":[{\"ett\":\"1585444657501\",\"en\":\"display\",\"kv\":{\"goodsid\":\"257\",\"action\":\"2\",\"extend1\":\"1\",\"place\":\"3\",\"category\":\"56\"}},{\"ett\":\"1585465752177\",\"en\":\"newsdetail\",\"kv\":{\"entry\":\"2\",\"goodsid\":\"258\",\"news_staytime\":\"6\",\"loading_time\":\"8\",\"action\":\"4\",\"showtype\":\"5\",\"category\":\"93\",\"type1\":\"\"}},{\"ett\":\"1585439817367\",\"en\":\"notification\",\"kv\":{\"ap_time\":\"1585488954035\",\"action\":\"4\",\"type\":\"4\",\"content\":\"\"}}]}";
        String x = new BaseFieldUDF().evaluate(line, "mid,uid,vc,vn,l,sr,os,ar,md,ba,sv,g,hw,nw,ln,la,t");
        System.out.println(x);
    }
    public String evaluate(String line, String jsonkeysString) {
        // 0 准备一个 sb
        StringBuilder sb = new StringBuilder();
        //1,切割jsonkeys得到mid等
        String[] jsonkeys = jsonkeysString.split(",");
        //2.处理line 服务器时间|json
        String[] logContents = line.split("\\|");
        //3.合法性校验
        if(logContents.length != 2 || StringUtils.isBlank(logContents[1])){
        return "";
        }
        //4.开始处理json
        try {
            JSONObject jsonObject = new JSONObject(logContents[1]);
            //获取cm里面的数据
            JSONObject base = jsonObject.getJSONObject("cm");
            //循环遍历取值
            for (int i = 0; i <jsonkeys.length; i++) {
                String filedName = jsonkeys[i].trim();
                if(base.has(filedName)){
                    sb.append(base.getString(filedName)).append("\t");
                }else{
                    sb.append("\t");
                }
            }
            sb.append(jsonObject.getString("et")).append("\t");
            sb.append(logContents[0]).append("\t");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
