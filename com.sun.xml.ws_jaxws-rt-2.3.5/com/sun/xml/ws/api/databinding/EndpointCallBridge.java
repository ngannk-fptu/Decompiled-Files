/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;

public interface EndpointCallBridge {
    public JavaCallInfo deserializeRequest(Packet var1);

    public Packet serializeResponse(JavaCallInfo var1);

    public JavaMethod getOperationModel();
}

