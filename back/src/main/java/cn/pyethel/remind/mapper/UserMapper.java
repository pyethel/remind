package cn.pyethel.remind.mapper;

import cn.pyethel.remind.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * date: 2020/12/6 13:21
 *
 * @author pyethel
 */
@Mapper
@Repository
public interface UserMapper {
    /**
     * openid查询user
     *
     * @param openid 用户唯一标示
     * @return user
     */
    @Select("select * from vx_users where openid=#{openid}")
    User getUserByOpenid(String openid);

    /**
     * token查询user
     *
     * @param token 令牌
     * @return user
     */
    @Select("select * from vx_users where token=#{token}")
    User getUserByToken(String token);

    /**
     * openid token保存用户
     *
     * @param openid 用户唯一标示
     * @param token  令牌
     */
    @Insert("insert into vx_users(openid, token) values(#{openid},#{token})")
    void saveUser(String openid, String token);

}
