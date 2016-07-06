package io.ganguo.chat.route.server.dto;

import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.core.transport.DataBuffer;
import io.ganguo.chat.core.transport.IMSerializer;

/**
 * Created by Tony on 2/20/15.
 */
public class MessageDTO implements IMSerializer {

    private Message message;

    public MessageDTO() {

    }

    public MessageDTO(Message message) {
        this.message = message;
    }

    /*public Long getTo() {
        return message.getTo();
    }*/

    public String getTo() {
        return message.getTo();
    }

    public String getFrom() {
        return message.getFrom();
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(message.getId());
        buffer.writeString(message.getTo());
        buffer.writeString(message.getFrom());
        buffer.writeByte(message.getType());
        buffer.writeString(message.getMessage());
        buffer.writeLong(message.getCreateAt());
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        if (message == null) {
            message = new Message();
        }
        message.setId(buffer.readString());
        message.setTo(buffer.readString());
        message.setFrom(buffer.readString());
        message.setType(buffer.readByte());
        message.setMessage(buffer.readString());
        message.setCreateAt(buffer.readLong());
    }
}
