/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

public class ClassUtility {
    public static String capitalize(String name) {
        int len = name.length();
        StringBuffer sb = new StringBuffer(len);
        boolean setCap = true;
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            if (c == '-' || c == '_') {
                setCap = true;
                continue;
            }
            if (setCap) {
                sb.append(Character.toUpperCase(c));
                setCap = false;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String toLowerCase(String name) {
        int len = name.length();
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String finalstaticOf(String membername) {
        int len = membername.length();
        StringBuffer sb = new StringBuffer(len + 2);
        for (int i = 0; i < len; ++i) {
            char c = membername.charAt(i);
            if (Character.isLowerCase(c)) {
                sb.append(Character.toUpperCase(c));
                continue;
            }
            if (Character.isUpperCase(c)) {
                sb.append('_').append(c);
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String packageOf(String fullclassname) {
        int index = fullclassname.lastIndexOf(".");
        if (index > 0) {
            return fullclassname.substring(0, index);
        }
        return "";
    }

    public static String classOf(String fullclassname) {
        int index = fullclassname.lastIndexOf(".");
        if (index > 0) {
            return fullclassname.substring(index + 1);
        }
        return fullclassname;
    }
}

