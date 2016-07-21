package io.ganguo.chat.route.server.dto;

import io.ganguo.chat.core.transport.DataBuffer;
import io.ganguo.chat.core.transport.IMSerializer;
import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.entity.Login;

/**
 * Created by user on 2016/7/21.
 */
public class FriendDTO implements IMSerializer {
    private Friend friend;

    public FriendDTO(){

    }

    public FriendDTO(Friend friend){
        this.friend = friend;
    }

    public Friend getFriend(){
        return friend;
    }

    public void setFriend(Friend friend){
        this.friend = friend;
    }

    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(friend.getUserName());
        buffer.writeString(friend.getFriendUserName());
        return buffer;
    }

    public void decode(DataBuffer buffer, short version) {
        if(friend == null) {
            friend = new Friend();
        }
        friend.setUserName(buffer.readString());
        friend.setFriendUserName(buffer.readString());
    }
}
