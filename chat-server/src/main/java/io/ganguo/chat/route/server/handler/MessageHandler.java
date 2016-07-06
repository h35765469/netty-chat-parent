package io.ganguo.chat.route.server.handler;

import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.route.biz.service.impl.MessageServiceImpl;
import io.ganguo.chat.route.server.dto.AckDTO;
import io.ganguo.chat.route.server.dto.FileDTO;
import io.ganguo.chat.route.server.dto.MessageDTO;
import io.ganguo.chat.route.server.file.ServerFile;
import io.ganguo.chat.route.server.session.ClientSession;
import io.ganguo.chat.route.server.session.ClientSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 2/20/15.
 */
@Component
public class MessageHandler extends IMHandler<IMRequest> {

    @Override
    public short getId() {
        return Handlers.MESSAGE;
    }

    @Autowired
    MessageServiceImpl messageService;

    private String FILE_SAVE_PATH = "D:";
    private int DATA_LENGTH = 1024;


    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.USER_MESSAGE_REQUEST:
    sendUserMessage(connection, request);
    break;
    case Commands.USER_MESSAGE_SUCCESS:
    onUserMessageSuccess(connection, request);
    break;
    case Commands.USER_FILE_REQUEST:
    sendUserFile(connection,request);
    break;
    case Commands.USER_FILE_SUCCESS:
    onUserFileSuccess(connection,request);
    break;
    default:
            connection.close();
    break;
}
}

    private void sendUserMessage(IMConnection connection, IMRequest request) {
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message message = messageDTO.getMessage();
        message.setCreateAt(System.currentTimeMillis());

        //此段確保了要給的to對象一定要在線上
        ClientSession session = ClientSessionManager.getInstance().get(messageDTO.getTo());
        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        if (session != null) {
            resp.setHeader(request.getHeader());
            resp.writeEntity(messageDTO);
            session.getConnection().sendResponse(resp);
        } else {
            //將離線訊息存在mongodb裡
            messageService.SaveOfflineMessage(messageDTO.getFrom(),messageDTO.getTo(),messageDTO.getMessage().getMessage());
            header.setCommandId(Commands.ERROR_USER_NOT_EXISTS);
            resp.setHeader(request.getHeader());
            connection.sendResponse(resp);

        }

    }

    private void onUserMessageSuccess(IMConnection connection, IMRequest request) {
        AckDTO ack = request.readEntity(AckDTO.class);

        ClientSession session = ClientSessionManager.getInstance().get(ack.getTo());
        IMResponse resp = new IMResponse();
        resp.setHeader(request.getHeader());
        resp.writeEntity(ack);
        session.getConnection().sendResponse(resp);
    }


    private void sendUserFile(IMConnection connection , IMRequest request){
        FileDTO fileDTO = request.readEntity(FileDTO.class);
        ServerFile serverFile = fileDTO.getServerFile();
        int sumCountPackage = serverFile.getSumCountPackage();
        int countPackage = serverFile.getCountPackage();
        byte[] bytes = serverFile.getBytes();
        String fileName = serverFile.getFileName();
        String path = FILE_SAVE_PATH + File.separator + fileName;
        File file = new File(path);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * DATA_LENGTH - DATA_LENGTH);
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        ClientSession session = ClientSessionManager.getInstance().get(serverFile.getReceiveId());
        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        if(session != null) {
            resp.setHeader(header);
            resp.writeEntity(fileDTO);
            session.getConnection().sendResponse(resp);
        }
        /*if(countPackage <= sumCountPackage){
            serverFile.setCountPackage(countPackage);
            IMResponse resp = new IMResponse();
            Header header = new Header();
            header.setHandlerId(Handlers.MESSAGE);
            header.setCommandId(Commands.USER_FILE_SUCCESS);
            resp.setHeader(header);
            resp.writeEntity(new AckDTO("123", serverFile.getId()));
            connection.sendResponse(resp);
        }*/
        //此段確保了要給的to對象一定要在線上
        /*ClientSession session = ClientSessionManager.getInstance().get(serverFile.getReceiveId());
        IMResponse resp = new IMResponse();
        Header header = request.getHeader();
        if(session != null){
            resp.setHeader(request.getHeader());
            resp.writeEntity(fileDTO);
            session.getConnection().sendResponse(resp);
        }*/


    }

    private void onUserFileSuccess(IMConnection connection , IMRequest request){
        AckDTO ack = request.readEntity(AckDTO.class);

        ClientSession session = ClientSessionManager.getInstance().get(ack.getTo());
        IMResponse resp = new IMResponse();
        resp.setHeader(request.getHeader());
        resp.writeEntity(ack);
        session.getConnection().sendResponse(resp);
    }



}
