package io.ganguo.chat.route.biz.entity;

/**
 * Created by user on 2016/7/6.
 */
public class OfflineMessage  extends  BaseEntity {
    private String[] offlineMessageArray;

    public String[] getOfflineMessageArray(){
        return offlineMessageArray;
    }

    public void setOfflineMessageArray(String[] offlineMessageArray){
        this.offlineMessageArray = offlineMessageArray;
    }
}
