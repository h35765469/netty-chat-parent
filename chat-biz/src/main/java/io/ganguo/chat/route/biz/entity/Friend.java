package io.ganguo.chat.route.biz.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by user on 2016/7/19.
 */
@Document
public class Friend extends  BaseEntity{

    private String friendUserName;
    private String friendName = "";
    private String username;
    private String[] friendArray;
    private boolean isFavorite = false;
    private boolean isBlock = false;

    public Friend(){

    }

    public Friend(String username , String friendUserName){
        this.username = username;
        this.friendUserName = friendUserName;
    }

    public String getFriendUserName(){
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName){
        this.friendUserName = friendUserName;
    }

    public String getFriendName(){
        return friendName;
    }

    public void setFriendName(String friendName){
        this.friendName = friendName;
    }

    public String getUserName(){
        return username;
    }

    public void setUserName(String username){
        this.username = username;
    }

    public String[] getFriendArray(){
        return friendArray;
    }

    public void setFriendArray(String[] friendArray){
        this.friendArray = friendArray;
    }

    public boolean getIsFavorite(){
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }

    public boolean getIsBlock(){
        return isBlock;
    }

    public void setIsBlock(boolean isBlock){
        this.isBlock = isBlock;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "username=" + username +
                ", friendusername=" + friendUserName +
                ", friendName=" + friendName +
                ", Favorite=" + isFavorite +
                ", Block=" + isBlock +
                '}';
    }
}
