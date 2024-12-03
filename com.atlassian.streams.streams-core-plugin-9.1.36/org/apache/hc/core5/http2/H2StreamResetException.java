/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2;

import org.apache.hc.core5.http.HttpStreamResetException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.util.Args;

public class H2StreamResetException
extends HttpStreamResetException {
    private final int code;

    public H2StreamResetException(H2Error error, String message) {
        super(message);
        Args.notNull(error, "H2 Error code");
        this.code = error.getCode();
    }

    public H2StreamResetException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

