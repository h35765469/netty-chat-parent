package io.ganguo.chat.route.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

/**
 * Created by user on 2016/2/2.
 */
@Configuration
@ComponentScan("io.ganguo.chat")
public class ClientStart {
    private static Logger logger = LoggerFactory.getLogger(ClientStart.class);

    public static void main(String[] args) {
        ChatClient chatClient = Client_Context.getBean(ChatClient.class);
        Scanner scanner = new Scanner(System.in);
        String input[] = scanner.next().split("#");
        try {
            chatClient.run(input[0],input[1]);
        } catch (Exception e) {
            logger.error("startup ChatServer error!!!", e);
        }
    }
}
