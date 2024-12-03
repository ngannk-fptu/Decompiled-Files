/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.message;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.FilterMessageImpl;
import com.sun.xml.ws.api.message.Message;
import javax.xml.namespace.QName;

public class FaultMessage
extends FilterMessageImpl {
    @Nullable
    private final QName detailEntryName;

    public FaultMessage(Message delegate, @Nullable QName detailEntryName) {
        super(delegate);
        this.detailEntryName = detailEntryName;
    }

    @Override
    @Nullable
    public QName getFirstDetailEntryName() {
        return this.detailEntryName;
    }
}

