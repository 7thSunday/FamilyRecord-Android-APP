package com.example.administrator.familyrecord.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 3/28/2018.
 */

public class HttpUtils {

    public static String Connecter (String url ,String outputdata){
        String msg = "";
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //设置请求方式,请求超时信息
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            //设置运行输入,输出:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //Post方式不能缓存,需手动设置为false
            conn.setUseCaches(false);
            //我们请求的数据:
            //获取输出流
            OutputStream out = conn.getOutputStream();
            out.write(outputdata.getBytes());
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
                return msg;
            }
        }catch(Exception e){e.printStackTrace();}
        return msg;
    }

    public static  JSONObject sign(JSONObject user,String url, int type){
        String msg = "";
        JSONObject result = new JSONObject();

        msg = Connecter(url,JsonToHttpString(user));

        try {
            if (type==0){
                //将字符串处理为Json对象
                JSONObject receivedata = new JSONObject(msg);

                JSONArray arr = null;
                arr = new JSONArray(receivedata.getString("data"));//提取数据部分
                for (int i = 0; i < arr.length(); i++) {
                    result = (JSONObject) arr.get(i);
                }
            }

            if (type==1){
                result = new JSONObject(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;//返回的Json对象
    }

    public static JSONObject creatFG (String url , JSONObject fg){

        String msg = "";
        JSONObject result = new JSONObject();
        try {
            msg =  Connecter(url,JsonToHttpString(fg));
            result = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject update (String url, JSONObject user){
        String msg = "";
        JSONObject result = new JSONObject();
        try {
            msg =  Connecter(url,JsonToHttpString(user));
            result = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static  JSONArray selectAlbum (String url, JSONObject user){

        JSONArray arr = null;
        try {
            JSONObject receivedata = new JSONObject(Connecter(url,JsonToHttpString(user)));

            arr = new JSONArray(receivedata.getString("data"));//提取数据部分

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;
    }

    public static JSONObject uploadHeadImage (String url,JSONObject user){
        JSONObject result = new JSONObject();
        try {
            JSONObject receivedata = new JSONObject(Connecter(url,JsonToHttpString(user)));

            result = new JSONObject(receivedata.getString("data"));//提取数据部分
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static JSONObject createAlbum (String url,JSONObject newAlbum){
        String msg = "";
        JSONObject result = new JSONObject();
        try {
            msg =  Connecter(url,JsonToHttpString(newAlbum));
            result = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray selectVideo (String url,JSONObject user){
        JSONArray arr = null;
        try {
            JSONObject receivedata = new JSONObject(Connecter(url,JsonToHttpString(user)));

            arr = new JSONArray(receivedata.getString("data"));//提取数据部分

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;
    }

    public static JSONArray selectMember (String url, JSONObject family){
        JSONArray arr = null;
        try {
            JSONObject receivedata = new JSONObject(Connecter(url,JsonToHttpString(family)));

            arr = new JSONArray(receivedata.getString("data"));//提取数据部分

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;
    }

    public static JSONObject addMember (String url,JSONObject newMember){
        String msg = "";
        JSONObject result = new JSONObject();
        try {
            msg =  Connecter(url,JsonToHttpString(newMember));
            result = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject deleteMember (String url,JSONObject delete){
        String msg = "";
        JSONObject result = new JSONObject();
        try {
            msg =  Connecter(url,JsonToHttpString(delete));
            result = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void upload (String url,String srcPath, JSONObject user){
        final File file=new File(srcPath);
        final String TAG = "uploadFile";
        final int TIME_OUT = 10*10000000; //超时时间
        final String CHARSET = "utf-8"; //设置编码
        final String BOUNDARY = "---WebKitFormBoundaryvc8a4RBu1k5VefTh" ; //UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\n";
        final String PREFIX="--";
        final String LINE_END="\r\n";
        final String CONTENT_TYPE = "multipart/form-data"; //内容类型

        /** * android上传文件到服务器
         * @param file 需要上传的文件
         * @param requestURL 请求的rul
         * @return 返回响应的内容
         */

            try {
                //URL url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                conn.setRequestProperty("Charset", CHARSET);//设置编码
                //头信息
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);




                if(file!=null) {
                    /** * 当文件不为空，把文件包装并且上传 */
                    OutputStream outputSteam=conn.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(outputSteam);

                    StringBuffer sb = new StringBuffer();




                    try {
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"type\"");
                        sb.append(LINE_END+LINE_END);
                        sb.append(user.get("type"));
                        sb.append(LINE_END);

                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"creator\"");
                        sb.append(LINE_END+LINE_END);
                        sb.append(user.get("creator"));
                        sb.append(LINE_END);

                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"rId\"");
                        sb.append(LINE_END+LINE_END);
                        sb.append(user.get("rId"));
                        sb.append(LINE_END);

                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dos.write(sb.toString().getBytes());
                    sb.append("Content-Disposition: form-data; name=\"file000\"; filename=\"" + file.getName().toString() +"\"");
                    sb.append(LINE_END);
                    sb.append("Content-Type: application/octet-stream");
                    sb.append(LINE_END+LINE_END);

                    System.out.println(sb.toString());
                    dos.write(sb.toString().getBytes());

                    //读取文件的内容
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while((len=is.read(bytes))!=-1)
                    {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    //写入文件二进制内容
                    dos.write(LINE_END.getBytes());
                    //写入end data
                    byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                    dos.write(end_data);
                    dos.flush();
                    /**
                     * 获取响应码 200=成功
                     * 当响应成功，获取响应的流
                     */
                    int res = conn.getResponseCode();
                    Log.e(TAG, "response code:"+res);
                    if(res==200) {
                        String oneLine;
                        StringBuffer response = new StringBuffer();
                        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((oneLine = input.readLine()) != null) {
                            response.append(oneLine);
                        }
                        //return response.toString();
                    }else{
                       // return res+"";
                    }
                }else{
                    //return "file not found";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //return "failed";
            } catch (IOException e) {
                e.printStackTrace();
               // return "failed";
            }

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



   /* public static  String DataToJsonString(String str){
        String ok = "";
        for (int i=0;i<str.length();i++){
            if (str.charAt(i)!='['&&str.charAt(i)!=']') {
                ok+= str.charAt(i);
            }
        }
        return ok;
    }*/

}
