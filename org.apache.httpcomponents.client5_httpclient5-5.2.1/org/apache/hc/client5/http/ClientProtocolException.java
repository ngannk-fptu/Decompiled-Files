/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http;

import java.io.IOException;

public class ClientProtocolException
extends IOException {
    private static final long serialVersionUID = -5596590843227115865L;

    public ClientProtocolException() {
    }

    public ClientProtocolException(String s) {
        super(s);
    }

    public ClientProtocolException(Throwable cause) {
        this.initCause(cause);
    }

    public ClientProtocolException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

