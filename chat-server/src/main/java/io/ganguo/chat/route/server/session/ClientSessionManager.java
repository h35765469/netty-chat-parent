package io.ganguo.chat.route.server.session;

import io.ganguo.chat.route.biz.bean.Presence;
import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.route.server.event.EventDispatcherManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tony on 2/21/15.
 */
//從userhandler
public class ClientSessionManager {

    //private final Map<Long, ClientSession> mSessions;
    private final Map<String, ClientSession> mSessions;
    private static ClientSessionManager mInstance;

    /*private ClientSessionManager() {
        mSessions = new ConcurrentHashMap<Long, ClientSession>();
    }*/
    private ClientSessionManager() {
        mSessions = new ConcurrentHashMap<String, ClientSession>();
    }

    public static ClientSessionManager getInstance() {
        if (mInstance == null) {
            mInstance = new ClientSessionManager();
        }
        return mInstance;
    }

    public void add(final ClientSession session) {
        //使用uin來代表身分
        //mSessions.put(session.getUin(), session);
        mSessions.put(session.getLogin().getAccount(), session);

        EventDispatcherManager.getPresenceEventDispatcher().availableSession(session);

        session.getConnection().registerCloseListener(new IMConnection.ConnectionCloseListener() {

            public void onClosed(IMConnection connection) {
                mSessions.remove(session.getLogin().getAccount());
                session.getPresence().setMode(Presence.Mode.UNAVAILABLE.value());
                EventDispatcherManager.getPresenceEventDispatcher().unavailableSession(session);
            }
        });
    }

    /*public ClientSession get(long uin) {
        return mSessions.get(uin);
    }*/
    public ClientSession get(String account) {
        return mSessions.get(account);
    }

    /*public Map<Long, ClientSession> sessions() {
        return mSessions;
    }*/

    public Map<String, ClientSession> sessions() {
        return mSessions;
    }

}
