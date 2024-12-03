/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.config;

public enum H2Param {
    HEADER_TABLE_SIZE(1),
    ENABLE_PUSH(2),
    MAX_CONCURRENT_STREAMS(3),
    INITIAL_WINDOW_SIZE(4),
    MAX_FRAME_SIZE(5),
    MAX_HEADER_LIST_SIZE(6);

    int code;
    private static final H2Param[] LOOKUP_TABLE;

    private H2Param(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static H2Param valueOf(int code) {
        if (code < 1 || code > LOOKUP_TABLE.length) {
            return null;
        }
        return LOOKUP_TABLE[code - 1];
    }

    public static String toString(int code) {
        if (code < 1 || code > LOOKUP_TABLE.length) {
            return Integer.toString(code);
        }
        return LOOKUP_TABLE[code - 1].name();
    }

    static {
        LOOKUP_TABLE = new H2Param[6];
        H2Param[] h2ParamArray = H2Param.values();
        int n = h2ParamArray.length;
        for (int i = 0; i < n; ++i) {
            H2Param param;
            H2Param.LOOKUP_TABLE[param.code - 1] = param = h2ParamArray[i];
        }
    }
}

