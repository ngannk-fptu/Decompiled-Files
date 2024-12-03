/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2;

import java.io.IOException;

public class H2CorruptFrameException
extends IOException {
    private static final long serialVersionUID = 1L;

    public H2CorruptFrameException(String message) {
        super(message);
    }
}

