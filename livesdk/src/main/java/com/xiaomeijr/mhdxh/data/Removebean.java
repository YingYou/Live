package com.xiaomeijr.mhdxh.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wuwei on 2017/9/10.
 */

public class Removebean implements Serializable{

    /**
     * code : 200
     * users : [{"time":"2015-09-25 16:12:38","userId":"2582"}]
     */

    private int code;
    private List<UsersBean> users;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    public static class UsersBean {
        /**
         * time : 2015-09-25 16:12:38
         * userId : 2582
         */

        private String time;
        private String userId;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
