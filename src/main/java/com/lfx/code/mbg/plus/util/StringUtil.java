package com.lfx.code.mbg.plus.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 23:05:26
 */
public class StringUtil {

    public static String toLowerCaseOfFirst(String str) {
        //如果字符串str为null和""则返回原数据
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        if (str.length() == 1) {
            //如果字符串str的长度为1，则调用专门把字符串转换为小写的string方法tuUpperCase()
            return str.toLowerCase();
        }
        //用字符串截取方法subString()截取第一个字符并调用toUpperCase()方法把它转换为小写字母
        //再与从str第二个下标截取的字符串拼接
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static void main(String[] args) {
        String pattern = "\\_\\d*$";
        System.out.println(pattern);
        System.out.println("t_user_1".replaceFirst(pattern, ""));
        System.out.println("t_user-1".replaceFirst(pattern, ""));
        System.out.println("t_user_01".replaceFirst(pattern, ""));
        System.out.println("t_user_10".replaceFirst(pattern, ""));
        System.out.println("t_user_1a".replaceFirst(pattern, ""));
    }
}
