package io.ganguo.chat.route.biz.service.impl;

import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.entity.Login;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.biz.repository.LoginRepository;
import io.ganguo.chat.route.biz.repository.UserRepository;
import io.ganguo.chat.route.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class  UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginRepository loginRepository;




    //註冊
    public void register(String account, long uin){
        //userRepository.deleteAll();
        if(userRepository.findByAccount(account) == null) {
            userRepository.save(new User(account, uin));
        }
    }


    /**
     * 登入
     *
     * @param account
     * @return
     */


    public Login login(String account) {
        User user = userRepository.findByAccount(account);
        if (user != null) {
            Login login = loginRepository.findByUin(user.getUin());
            if (login == null) {
                login = new Login();
            }
            login.setUin(user.getUin());
            login.setAccount(account);
            login.setAuthToken(UUID.randomUUID().toString());
            login.setActiveTime(System.currentTimeMillis());
            loginRepository.save(login);
            return login;
        }
        return null;
    }


    public User findByUin(long uin) {
        return userRepository.findByUin(uin);
    }

    /**
     * 验证 Token
     *
     * @param uin
     * @param token
     * @return
     */

    public boolean authenticate(Long uin, String token) {
        Login login = loginRepository.findByUin(uin);
        if (login != null && token != null) {
            return login.getAuthToken().equals(token);
        }
        return false;
    }


}
