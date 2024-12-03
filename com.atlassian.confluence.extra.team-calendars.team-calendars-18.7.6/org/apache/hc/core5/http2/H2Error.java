/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum H2Error {
    NO_ERROR(0),
    PROTOCOL_ERROR(1),
    INTERNAL_ERROR(2),
    FLOW_CONTROL_ERROR(3),
    SETTINGS_TIMEOUT(4),
    STREAM_CLOSED(5),
    FRAME_SIZE_ERROR(6),
    REFUSED_STREAM(7),
    CANCEL(8),
    COMPRESSION_ERROR(9),
    CONNECT_ERROR(10),
    ENHANCE_YOUR_CALM(11),
    INADEQUATE_SECURITY(12),
    HTTP_1_1_REQUIRED(13);

    int code;
    private static final ConcurrentMap<Integer, H2Error> MAP_BY_CODE;

    private H2Error(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static H2Error getByCode(int code) {
        return (H2Error)((Object)MAP_BY_CODE.get(code));
    }

    static {
        MAP_BY_CODE = new ConcurrentHashMap<Integer, H2Error>();
        for (H2Error error : H2Error.values()) {
            MAP_BY_CODE.putIfAbsent(error.code, error);
        }
    }
}

