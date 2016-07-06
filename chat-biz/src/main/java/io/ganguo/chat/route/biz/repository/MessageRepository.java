package io.ganguo.chat.route.biz.repository;

import io.ganguo.chat.route.biz.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Tony on 2/23/15.
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, String> {

    List<Message> findByTo(String To);

}
