package io.ganguo.chat.route.biz.service;

import io.ganguo.chat.route.biz.entity.Message;

import java.util.List;

/**
 * Created by Tony on 2/23/15.
 */
public interface MessageService {

    public void SaveOfflineMessage(String From, String To , String Message);
    public List<Message> GetOfflineMessage(String To);
    public void RemoveOfflineMessage(String To);

    List<Message> findByTo(String To);
}
