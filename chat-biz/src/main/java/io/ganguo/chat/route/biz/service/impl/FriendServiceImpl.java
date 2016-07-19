package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.entity.Friend;
import io.ganguo.chat.route.biz.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by user on 2016/7/19.
 */
@Service
public class FriendServiceImpl {
    @Autowired
    private FriendRepository friendRepository;

    public Friend findByUsername(String userName){
        return friendRepository.findByUsername(userName);
    }
}
