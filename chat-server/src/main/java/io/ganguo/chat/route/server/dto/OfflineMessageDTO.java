package io.ganguo.chat.route.server.dto;

import io.ganguo.chat.core.transport.DataBuffer;
import io.ganguo.chat.core.transport.IMSerializer;
import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.route.biz.entity.OfflineMessage;

/**
 * Created by user on 2016/7/6.
 */
public class OfflineMessageDTO implements IMSerializer {

    OfflineMessage offlineMessage;

   public OfflineMessageDTO(OfflineMessage offlineMessage){
       this.offlineMessage = offlineMessage;
   }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeStringArray(offlineMessage.getOfflineMessageArray());
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        if(offlineMessage == null){
            offlineMessage = new OfflineMessage();
        }
        offlineMessage.setOfflineMessageArray(buffer.readStringArray());
    }
}
