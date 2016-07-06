package io.ganguo.chat.route.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * Created by user on 2016/2/2.
 */
public class Client_Context {
    private final ApplicationContext applicationContext;
    private final static Client_Context singleton = new Client_Context();

    private static Client_Context me() {
        return singleton;
    }

    private Client_Context() {
        applicationContext = new AnnotationConfigApplicationContext(ClientStart.class);
    }

    public static <T> T getBean(Class<T> clazz) {
        return singleton.applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name) {
        return (T) singleton.applicationContext.getBean(name);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return singleton.applicationContext.getBeansOfType(type);
    }
}
