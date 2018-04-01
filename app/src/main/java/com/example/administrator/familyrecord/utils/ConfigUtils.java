package com.example.administrator.familyrecord.utils;

import android.content.Context;
import java.io.InputStream;
import java.util.Properties;


public class ConfigUtils {

    static String pro_value;

    public static String getProperties(Context c, String proName) {
        Properties props = new Properties();
        try {
            InputStream in = c.getAssets().open("appconfig.properties");
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        pro_value = props.getProperty(proName);
        return pro_value;

    }
}
