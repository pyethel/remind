package cn.pyethel.remind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * date: 2020/12/5 23:24
 *
 * @author pyethel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String openid;
    private String token;
}
