/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;

class ConnectionException
extends Http2Exception {
    private static final long serialVersionUID = 1L;

    ConnectionException(String msg, Http2Error error) {
        super(msg, error);
    }

    ConnectionException(String msg, Http2Error error, Throwable cause) {
        super(msg, error, cause);
    }
}

