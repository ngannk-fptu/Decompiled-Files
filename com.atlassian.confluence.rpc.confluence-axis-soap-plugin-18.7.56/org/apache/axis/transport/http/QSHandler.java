/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

public interface QSHandler {
    public void invoke(MessageContext var1) throws AxisFault;
}

