/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.util.Properties;

public final class OutputPropertyUtils {
    public static boolean getBooleanProperty(String key, Properties props) {
        String s = props.getProperty(key);
        return null != s && s.equals("yes");
    }

    public static int getIntProperty(String key, Properties props) {
        String s = props.getProperty(key);
        if (null == s) {
            return 0;
        }
        return Integer.parseInt(s);
    }
}

