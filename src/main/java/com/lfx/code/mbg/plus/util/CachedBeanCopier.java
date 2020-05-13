package com.lfx.code.mbg.plus.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:linfx@dydf.cn">linfuxin</a>
 * @date 2020-05-13 11:20:05
 */
@Slf4j
@UtilityClass
public class CachedBeanCopier {
    private static final Map<String, BeanCopier> BEAN_COPIERS = new HashMap<>();
    private static final String KEY_SPLICE = "-";

    public static <S, T> BeanCopier registerToCache(Class<S> sourceClass, Class<T> targetClass) {
        String key = genKey(sourceClass, targetClass);
        BeanCopier copier = null;
        if (!BEAN_COPIERS.containsKey(key)) {
            copier = BeanCopier.create(sourceClass, targetClass, false);
            BEAN_COPIERS.put(key, copier);
        } else {
            copier = BEAN_COPIERS.get(key);
        }
        return copier;
    }

    public static <S, T> T copy(S source, Class<T> targetClazz) {
        return copy(source, targetClazz, null);
    }

    public static <S, T> T copy(S source, Class<T> targetClazz, Converter converter) {
        if (source == null || targetClazz == null) {
            return null;
        }
        return registerAndCopy(source, ObjenesisUtil.newInstance(targetClazz), converter);
    }

    private static <S, T> T registerAndCopy(S source, T target, Converter converter) {
        BeanCopier copier = registerToCache(source.getClass(), target.getClass());
        copier.copy(source, target, converter);
        return target;
    }

    private static String genKey(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.getName() + KEY_SPLICE + targetClass.getName();
    }

}
