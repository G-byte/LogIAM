package com.didichuxing.datachannel.agentmanager.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengqiao
 * @date 20/4/16
 */
public class ValidateUtils {
    /**
     * 为空
     */
    public static boolean isNull(Object object) {
        if (object instanceof String) {
            String str = (String) object;
            return isBlank(str);
        }

        return object == null;
    }

    /**
     * 是空字符串或者空
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 存在空
     */
    public static boolean isExistBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是空字符串
     */
    public static boolean equalList(List<Object> seq1, List<Object> seq2) {
        if (isNull(seq1) && isNull(seq2)) {
            return true;
        } else if (isNull(seq1) || isNull(seq2) || seq1.size() != seq2.size()) {
            return false;
        }
        for (Object elem: seq1) {
            if (!seq2.contains(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyList(List<?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isEmptySet(Set<?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isEmptyMap(Map<?, ?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isNullOrLessThanZero(Long value) {
        return value == null || value < 0;
    }

    public static boolean isNullOrLessThanZero(Integer value) {
        return value == null || value < 0;
    }

    public static boolean isNullOrLessThanZero(Double value) {
        return value == null || value < 0;
    }

}