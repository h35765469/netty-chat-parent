package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.repository.FriendRepository;
import io.ganguo.chat.route.biz.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public void favoriteFriend(String username , String friendUserName , int isFavorite){
        Query searchUserQuery = new Query(Criteria.where("username").is(username).andOperator(Criteria.where("friendUserName").is(friendUserName)));
        Update update = new Update();
        update.set("isFavorite", isFavorite);
        mongoTemplate.findAndModify(searchUserQuery , update , Friend.class);
    }

    public void blockFriend(String username , String friendUserName){
        Query searchUserQuery = new Query(Criteria.where("username").is(username).andOperator(Criteria.where("friendUserName").is(friendUserName)));
        Update update = new Update();
        update.set("isBlock", 0);
        mongoTemplate.findAndModify(searchUserQuery , update , Friend.class);
    }

    public void viewFriend(String username , String friendUserName , int viewer){
        Query searchUserQuery = new Query(Criteria.where("username").is(username).andOperator(Criteria.where("friendUserName").is(friendUserName)));
        Update update = new Update();
        update.set("viewer", viewer);
        mongoTemplate.findAndModify(searchUserQuery , update , Friend.class);
    }

    public void editFriendName(String username , String friendUserName , String friendName){
        Query searchUserQuery = new Query(Criteria.where("username").is(username).andOperator(Criteria.where("friendUserName").is(friendUserName)));
        Update update = new Update();
        update.set("friendName" , friendName);
        mongoTemplate.findAndModify(searchUserQuery , update , Friend.class);
    }


    public List<Friend>findByUsername(String username){
        return  friendRepository.findByUsername(username);
    }



}
