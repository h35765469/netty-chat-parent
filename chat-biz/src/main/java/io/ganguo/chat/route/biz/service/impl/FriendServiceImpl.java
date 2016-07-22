package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.repository.FriendRepository;
import io.ganguo.chat.route.biz.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by user on 2016/7/19.
 */
@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    //儲存朋友
    public void saveFriend(String username , String friendUsername){
        friendRepository.save(new Friend(username,friendUsername));
    }

    //獲取好友
    public List<Friend> getFriend(String username){
        if(!friendRepository.findByUsername(username).isEmpty()){
            return findByUsername(username);
        }
        return null;
    }

    public void removeFriend(String username , String friendUsername){
        List<Friend>list = getFriend(username);
        for(Friend friend : list){
            if(friend.getFriendUserName().equals(friendUsername)) {
                mongoTemplate.remove(friend);
            }
        }
    }

    public List<Friend>findByUsername(String username){
        return  friendRepository.findByUsername(username);
    }



}
