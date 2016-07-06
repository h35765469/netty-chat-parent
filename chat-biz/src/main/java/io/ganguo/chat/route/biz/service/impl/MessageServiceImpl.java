package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.route.biz.repository.MessageRepository;
import io.ganguo.chat.route.biz.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2/23/15.
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    //儲存離線訊息
    public void SaveOfflineMessage(String From, String To , String Message){
        messageRepository.save(new Message(From,To,Message));
    }

    //獲取離線訊息
    public List<Message> GetOfflineMessage(String To){
        if(!messageRepository.findByTo(To).isEmpty()) {
            List<Message> messageList = messageRepository.findByTo(To);
            return messageList;
        }
        return null;
    }

    //刪除離線訊息
    public void RemoveOfflineMessage(String To){
        List<Message>list = messageRepository.findByTo(To);
        System.out.println(mongoTemplate);
        if(list != null){
            for(Message message : list){
                mongoTemplate.remove(message);
            }
        }
    }

    //找到To的名字在mogdb裡
    public List<Message> findByTo(String To){
        return messageRepository.findByTo(To);
    }




}
