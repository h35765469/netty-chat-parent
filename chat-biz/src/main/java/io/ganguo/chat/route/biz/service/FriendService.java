package io.ganguo.chat.route.biz.service;

import io.ganguo.chat.route.biz.entity.Friend;

/**
 * Created by user on 2016/7/19.
 */
public interface FriendService {
    Friend findByUsername(String userName);
}
