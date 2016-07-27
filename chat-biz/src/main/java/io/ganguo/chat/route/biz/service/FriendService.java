package io.ganguo.chat.route.biz.service;

import io.ganguo.chat.route.biz.entity.Friend;

import java.util.List;

/**
 * Created by user on 2016/7/19.
 */
public interface FriendService {

    void saveFriend(String username , String frendUsername);
    List<Friend>getFriend(String username);
    void removeFriend(String username , String friendUsername);
    void favoriteFriend(String username,String friendUserName,int isFavorite);
    List<Friend> findByUsername(String username);
}
