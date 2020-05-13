package com.lfx.code.mbg.plus.util;


import lombok.experimental.UtilityClass;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * @author <a href="mailto:linfx@dydf.cn">linfuxin</a>
 * @date 2020-05-13 11:27:14
 */
@UtilityClass
public class ObjenesisUtil {
    private static final Objenesis OBJENESIS = new ObjenesisStd();

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        return (T) OBJENESIS.getInstantiatorOf(clazz).newInstance();
    }
}
