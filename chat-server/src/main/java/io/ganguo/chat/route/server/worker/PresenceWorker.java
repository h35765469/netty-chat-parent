package io.ganguo.chat.route.server.worker;

import io.ganguo.chat.route.biz.bean.Presence;
import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.core.util.TaskUtil;
import io.ganguo.chat.route.server.dto.PresenceDTO;
import io.ganguo.chat.route.server.session.ClientSession;
import io.ganguo.chat.route.server.session.ClientSessionManager;

import java.util.Map;

/**
 * Created by Tony on 2/24/15.
 */
//從presenceEventDispatcher來
public class PresenceWorker extends IMWorker<Presence> {

    public static final int PROCESS_DELAYER_MILLIS = 20;

    public PresenceWorker() {
        TaskUtil.pool(this);
    }

    @Override
    public void process(Presence presence) {
        // broadcast
        Map<String, ClientSession> sessions = ClientSessionManager.getInstance().sessions();

        //此邊的keySet會將map裡隨機的key值print出
        for (String account : sessions.keySet()) {
            if (presence.getAccount() != account) {
                ClientSession session = sessions.get(account);

                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.USER);
                header.setCommandId(Commands.USER_PRESENCE_CHANGED);
                resp.setHeader(header);
                resp.writeEntity(new PresenceDTO(presence));
                session.getConnection().sendResponse(resp);
            }
        }
        // 處理完當前server connection，还需要處理在其它server的用户
        // 把為發送的uin分发到route服务

        try {
            Thread.sleep(PROCESS_DELAYER_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
