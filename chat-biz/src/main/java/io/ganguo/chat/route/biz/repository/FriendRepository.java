package io.ganguo.chat.route.biz.repository;

import io.ganguo.chat.route.biz.entity.Friend;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 2016/7/19.
 */

@Repository
public interface FriendRepository  extends CrudRepository<Friend , String> {

    Friend findByUsername(String userName);
}
