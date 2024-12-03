/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.MDC
 *  org.slf4j.MDC
 */
package freemarker.log;

import org.slf4j.MDC;

public class _Log4jOverSLF4JTester {
    private static final String MDC_KEY = _Log4jOverSLF4JTester.class.getName();

    public static final boolean test() {
        org.apache.log4j.MDC.put((String)MDC_KEY, (String)"");
        try {
            boolean bl = MDC.get((String)MDC_KEY) != null;
            return bl;
        }
        finally {
            org.apache.log4j.MDC.remove((String)MDC_KEY);
        }
    }
}

