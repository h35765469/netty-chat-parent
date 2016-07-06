package io.ganguo.chat.route.client.handler;

import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.biz.service.impl.MessageServiceImpl;
import io.ganguo.chat.route.server.dto.AckDTO;
import io.ganguo.chat.route.server.dto.FileDTO;
import io.ganguo.chat.route.server.dto.MessageDTO;
import io.ganguo.chat.route.server.file.ServerFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.RandomAccessFile;


/**
 * Created by Tony on 2/20/15.
 */
@Component
public class Client_MessageHandler extends IMHandler<IMRequest> {

    @Autowired
    private MessageServiceImpl messageService;

    @Override
    public short getId() {
        return Handlers.MESSAGE;
    }

    private String FILE_SAVE_PATH = "D:\\GG";
    private int DATA_LENGTH = 1024;

    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.USER_MESSAGE_REQUEST:
                receiveMessage(connection, request);
                break;
            case Commands.USER_MESSAGE_SUCCESS:
                onUserMessageSuccess(connection, request);
                break;
            case Commands.USER_FILE_REQUEST:
                receiveFile(connection , request);
                break;
            case Commands.USER_FILE_SUCCESS:
                onUserFileSuccess(connection , request);
            case Commands.ERROR_USER_NOT_EXISTS: {
                System.out.println("用户不存在接收不到消息～～");
            }
            break;
            default:
                connection.close();
                break;
        }
    }

    private void receiveMessage(IMConnection connection, IMRequest request) {
        MessageDTO messageDTO = request.readEntity(MessageDTO.class);
        Message message = messageDTO.getMessage();

        System.out.println("message: " + message);

        // 回應告訴對方已經收到，如果对方接收不到回應需要重複發送消息，客户端也需要對重複的消息做處理
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_MESSAGE_SUCCESS);
        resp.setHeader(header);
        resp.writeEntity(new AckDTO("123", message.getId()));
        connection.sendResponse(resp);
    }

    private void onUserMessageSuccess(IMConnection connection, IMRequest request) {
        AckDTO ack = request.readEntity(AckDTO.class);
        System.out.println("onMessageSuccess: " + ack);
    }

    private void receiveFile(IMConnection connection , IMRequest request){
        FileDTO fileDTO = request.readEntity(FileDTO.class);
        ServerFile serverFile = fileDTO.getServerFile();
        int sumCountPackage = serverFile.getSumCountPackage();
        int countPackage = serverFile.getCountPackage();
        byte[] bytes = serverFile.getBytes();
        String fileName = serverFile.getFileName();
        String path = FILE_SAVE_PATH +  File.separator + fileName;
        File file = new File(path);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * DATA_LENGTH - DATA_LENGTH);
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void onUserFileSuccess(IMConnection connection , IMRequest request){}

}
