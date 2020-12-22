package cn.pyethel.remind.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * date: 2020/12/18 15:13
 *
 * @author pyethel
 */
public class Do2VoUtils {
    /**
     * List<DO>转List<VO>
     */
    public static <T> List<T> copyList(List<?> doList, Class<T> voClass) {
        List<T> voList = new ArrayList<>();
        try {
            T voObj = null;
            for (Object doObj : doList) {
                voObj = voClass.newInstance();
                BeanUtils.copyProperties(doObj, voObj);
                voList.add(voObj);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return voList;
    }

    /**
     * DO转VO
     */
    public static <T> T copyObj(Object dObject, Class<T> voClass) {
        T voObj = null;
        try {
            voObj = voClass.newInstance();
            BeanUtils.copyProperties(dObject, voObj);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return voObj;
    }
}
