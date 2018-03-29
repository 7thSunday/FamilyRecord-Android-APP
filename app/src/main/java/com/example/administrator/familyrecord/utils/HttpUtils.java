package com.example.administrator.familyrecord.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 3/28/2018.
 */

public class HttpUtils {

    public static  boolean sign(JSONObject user,String url)
    {
        String msg = "";
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //设置请求方式,请求超时信息
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            //设置运行输入,输出:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //Post方式不能缓存,需手动设置为false
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
//            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // 设置接收类型
            conn.setRequestProperty("accept","application/json");
            conn.connect();
            String data = JsonToHttpString(user);

            //获取输出流
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            /*out.write(data.getBytes());*/
            out.writeBytes(data);
            out.flush();
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                System.out.println(msg);
                JSONObject result = new JSONObject(msg);
                return result.getBoolean("success");
            }
        }catch(Exception e){e.printStackTrace();}
        return false;
    }

    public static String JsonToHttpString(JSONObject json){
        String str = "";
        Iterator itr = json.keys();
        while(itr.hasNext()){
            String key = (String) itr.next();
            try {
                str += key+"="+json.getString(key)+"&";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return str.equals("")?"":str.substring(0,str.length()-1);
    }

}
