/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote;

import org.apache.tomcat.util.res.StringManager;

public enum ContinueResponseTiming {
    IMMEDIATELY("immediately"),
    ON_REQUEST_BODY_READ("onRead"),
    ALWAYS("always");

    private static final StringManager sm;
    private final String configValue;

    public static ContinueResponseTiming fromString(String value) {
        if (IMMEDIATELY.toString().equalsIgnoreCase(value)) {
            return IMMEDIATELY;
        }
        if (ON_REQUEST_BODY_READ.toString().equalsIgnoreCase(value)) {
            return ON_REQUEST_BODY_READ;
        }
        throw new IllegalArgumentException(sm.getString("continueResponseTiming.invalid", new Object[]{value}));
    }

    private ContinueResponseTiming(String configValue) {
        this.configValue = configValue;
    }

    public String toString() {
        return this.configValue;
    }

    static {
        sm = StringManager.getManager(ContinueResponseTiming.class);
    }
}

