/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;

public class ProtocolNegotiationException
extends IOException {
    private static final long serialVersionUID = 6211774735704945037L;

    public ProtocolNegotiationException(String message) {
        super(message);
    }
}

