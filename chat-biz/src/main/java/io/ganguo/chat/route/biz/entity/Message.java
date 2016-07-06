package io.ganguo.chat.route.biz.entity;


import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Tony on 2/20/15.
 */
@Document
public class Message extends BaseEntity {
    // uin
    private String to;
    private String from;

    private byte type;
    private boolean read;
    private String message;
    private long createAt;
    private long readAt;

    public Message(){

    }

    public Message(String from , String to , String message){
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "to=" + to +
                ", from=" + from +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", createAt=" + createAt +
                '}';
    }

    public enum Type {
        SESSION_MSG(0), // 臨時會話消息
        USER_MSG(1),  // 好友消息
        ROOM_MSG(2);  // 群消息

        private byte mValue = 0;

        public byte getValue() {
            return mValue;
        }

        Type(int value) {
            mValue = (byte) value;
        }

        public static Type valueOfRaw(byte value) {
            for (Type type : Type.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return SESSION_MSG;
        }
    }

}