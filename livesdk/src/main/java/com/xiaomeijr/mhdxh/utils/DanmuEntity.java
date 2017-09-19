package com.xiaomeijr.mhdxh.utils;

/**
 * Created by wuwei on 2017/6/20.
 */
import com.orzangleli.xdanmuku.Model;

/**
 * Created by Administrator on 2017/3/30.
 */

public class DanmuEntity extends Model {
    public String content;
    public int textColor;
    public String time;
    public String name;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}