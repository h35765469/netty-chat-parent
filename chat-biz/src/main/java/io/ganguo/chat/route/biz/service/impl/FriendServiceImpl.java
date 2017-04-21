package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.biz.repository.FriendRepository;
import io.ganguo.chat.route.biz.repository.UserRepository;
import io.ganguo.chat.route.biz.service.FriendService;
import org.bson.types.ObjectId;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
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
    public void saveFriend(String loginId , String friendId, int status){
        Query query = new Query(Criteria.where("id").is(loginId));
        User userObject = mongoTemplate.findOne(query,User.class);
        query = new Query(Criteria.where("id").is(friendId));
        User friendObject = mongoTemplate.findOne(query, User.class);
        Friend friend = new Friend(status);
        friend.setUserObject(userObject);
        friend.setFriendObject(friendObject);
        friendRepository.save(friend);
    }

    /*更改朋友的status
        0:待確認中，1:好友，2:封鎖，3:拒絕好友邀請
    */
    public void updateFriendStatus(String loginId, String friendId, int status){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(friendId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(loginId))));
        Update update = new Update();
        update.set("status", status);
        mongoTemplate.findAndModify(query , update , Friend.class);
    }

    //獲取好友
    public List<Friend> getFriend(String loginId){
        Query query = new Query();
        query.addCriteria(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("status").is(1).orOperator(Criteria.where("status").is(0).orOperator(Criteria.where("status").is(4)))));
        return mongoTemplate.find(query, Friend.class);
    }

    //刪除好友
    public void removeFriend(String loginId, String friendId){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(friendId))));
        mongoTemplate.remove(query, Friend.class);
    }

    //最愛好友
    public void favoriteFriend(String loginId, String friendId , int isFavorite){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(friendId))));
        Update update = new Update();
        update.set("isFavorite", isFavorite);
        mongoTemplate.findAndModify(query , update , Friend.class);
    }

    //封解鎖好友
    public void blockFriend(String loginId, String friendId, int isBlock){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(friendId))));
        Update update = new Update();
        //update.set("block", isBlock);
        update.set("status",isBlock);
        mongoTemplate.findAndModify(query , update , Friend.class);
    }

    //開關觀察者
    public void viewFriend(String loginId, String friendId, int viewer){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(friendId))));
        Update update = new Update();
        update.set("viewer", viewer);
        mongoTemplate.findAndModify(query , update , Friend.class);
    }

    //編輯朋友姓名
    public void editFriendName(String loginId , String friendId , String friendName){
        Query query = new Query(Criteria.where("userObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("friendObject.$id").is(new ObjectId(friendId))));
        Update update = new Update();
        update.set("friendName" , friendName);
        mongoTemplate.findAndModify(query , update , Friend.class);
    }

    public List<User> searchFriendName(String friendName){

        Query query = new Query();
        query.limit(10);
        query.addCriteria(Criteria.where("nickName").regex(friendName));

        return mongoTemplate.find(query, User.class);
    }


    public List<Friend>findByUsername(String username){
        return  friendRepository.findByUsername(username);
    }


    //獲取待確認的好友
    public List<Friend>findPendingFriend(String loginId){
        Query query = new Query();
        query.addCriteria(Criteria.where("friendObject.$id").is(new ObjectId(loginId)).andOperator(Criteria.where("status").is(0)));
        return mongoTemplate.find(query, Friend.class);
    }

}
