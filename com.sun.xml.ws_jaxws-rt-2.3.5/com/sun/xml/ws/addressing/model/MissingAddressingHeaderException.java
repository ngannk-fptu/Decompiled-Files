/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing.model;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class MissingAddressingHeaderException
extends WebServiceException {
    private final QName name;
    private final transient Packet packet;

    public MissingAddressingHeaderException(@NotNull QName name) {
        this(name, null);
    }

    public MissingAddressingHeaderException(@NotNull QName name, @Nullable Packet p) {
        super(AddressingMessages.MISSING_HEADER_EXCEPTION(name));
        this.name = name;
        this.packet = p;
    }

    public QName getMissingHeaderQName() {
        return this.name;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

