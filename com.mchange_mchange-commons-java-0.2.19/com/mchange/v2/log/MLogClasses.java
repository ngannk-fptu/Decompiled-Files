/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MLogClasses {
    static final String LOG4J_CNAME = "com.mchange.v2.log.log4j.Log4jMLog";
    static final String LOG4J2_CNAME = "com.mchange.v2.log.log4j2.Log4j2MLog";
    static final String SLF4J_CNAME = "com.mchange.v2.log.slf4j.Slf4jMLog";
    static final String JDK14_CNAME = "com.mchange.v2.log.jdk14logging.Jdk14MLog";
    static final String[] SEARCH_CLASSNAMES = new String[]{"com.mchange.v2.log.slf4j.Slf4jMLog", "com.mchange.v2.log.log4j.Log4jMLog", "com.mchange.v2.log.log4j2.Log4j2MLog", "com.mchange.v2.log.jdk14logging.Jdk14MLog"};
    static final Map<String, String> ALIASES;

    static String resolveIfAlias(String string) {
        String string2 = ALIASES.get(string.toLowerCase());
        if (string2 == null) {
            string2 = string;
        }
        return string2;
    }

    private MLogClasses() {
    }

    static {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("log4j", LOG4J_CNAME);
        hashMap.put("log4j2", LOG4J2_CNAME);
        hashMap.put("slf4j", SLF4J_CNAME);
        hashMap.put("jdk14", JDK14_CNAME);
        hashMap.put("jul", JDK14_CNAME);
        hashMap.put("java.util.logging", JDK14_CNAME);
        hashMap.put("fallback", "com.mchange.v2.log.FallbackMLog");
        ALIASES = Collections.unmodifiableMap(hashMap);
    }
}

