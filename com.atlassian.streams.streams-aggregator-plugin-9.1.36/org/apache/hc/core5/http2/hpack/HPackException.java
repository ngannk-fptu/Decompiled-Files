/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http.HttpException;

public class HPackException
extends HttpException {
    private static final long serialVersionUID = 1L;

    public HPackException(String message) {
        super(message);
    }

    public HPackException(String message, Exception cause) {
        super(message, cause);
    }
}

