package io.ganguo.chat.route.biz.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Tony on 2/20/15.
 */
@Document
public class Login extends BaseEntity {
    //@Indexed(unique = true)
    private String account;
    private long uin;
    private String authToken;
    private long activeTime;

    public Login(){

    }

    public Login(long uin){
        this.uin = uin;
    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account){
        this.account = account;
    }

    public long getUin() {
        return uin;
    }

    public void setUin(long uin) {
        this.uin = uin;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    @Override
    public String toString() {
        return "Login{" +
                "uin=" + uin +
                ", account='" + account + '\'' +
                ", authToken='" + authToken + '\'' +
                ", activeTime=" + activeTime +
                '}';
    }
}
