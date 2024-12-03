/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import java.lang.reflect.Method;

public interface ClientCallBridge {
    public Packet createRequestPacket(JavaCallInfo var1);

    public JavaCallInfo readResponse(Packet var1, JavaCallInfo var2) throws Throwable;

    public Method getMethod();

    public JavaMethod getOperationModel();
}

