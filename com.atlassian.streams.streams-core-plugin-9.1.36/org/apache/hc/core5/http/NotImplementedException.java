/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.ProtocolException;

public class NotImplementedException
extends ProtocolException {
    private static final long serialVersionUID = 7929295893253266373L;

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }
}

