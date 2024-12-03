/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.ConnectionClosedException;

@Internal
public class RequestNotExecutedException
extends ConnectionClosedException {
    public RequestNotExecutedException() {
        super("Connection is closed");
    }

    public RequestNotExecutedException(String message) {
        super(message);
    }
}

