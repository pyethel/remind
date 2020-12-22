package cn.pyethel.remind.service;

import cn.pyethel.remind.entity.User;
import cn.pyethel.remind.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * date: 2020/12/5 23:26
 *
 * @author pyethel
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void saveUser(String openid, String token) {
        userMapper.saveUser(openid, token);
    }

    public User getUserByOpenId(String openid) {
        return userMapper.getUserByOpenid(openid);
    }

    public User getUserByToken(String token) {
        return userMapper.getUserByToken(token);
    }
}
