package io.ganguo.chat.route.biz.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by user on 2016/7/19.
 */
@Document
public class Friend {

    private String friendUserName;
    private String userName;

    public Friend(){

    }

    public String getFriendUserName(){
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName){
        this.friendUserName = friendUserName;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }


    @Override
    public String toString() {
        return "Friend{" +
                "UserName=" + userName +
                ", FriendUserName=" + friendUserName +
                '}';
    }
}
