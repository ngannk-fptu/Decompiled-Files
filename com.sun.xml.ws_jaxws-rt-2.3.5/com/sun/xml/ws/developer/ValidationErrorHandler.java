/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.api.message.Packet;
import org.xml.sax.ErrorHandler;

public abstract class ValidationErrorHandler
implements ErrorHandler {
    protected Packet packet;

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

