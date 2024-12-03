/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.util.Map;
import java.util.Properties;

public class PoolUtilities {
    public static final String PROP_USER = "user";
    public static final String PROP_PASSWORD = "password";

    public static Properties clone(Properties p) {
        Properties c = new Properties();
        c.putAll((Map<?, ?>)p);
        return c;
    }

    public static Properties cloneWithoutPassword(Properties p) {
        Properties result = PoolUtilities.clone(p);
        result.remove(PROP_PASSWORD);
        return result;
    }
}

