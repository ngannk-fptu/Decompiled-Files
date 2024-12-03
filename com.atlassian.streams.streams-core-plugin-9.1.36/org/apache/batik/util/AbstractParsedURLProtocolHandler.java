/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import org.apache.batik.util.ParsedURLProtocolHandler;

public abstract class AbstractParsedURLProtocolHandler
implements ParsedURLProtocolHandler {
    protected String protocol;

    public AbstractParsedURLProtocolHandler(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getProtocolHandled() {
        return this.protocol;
    }
}

