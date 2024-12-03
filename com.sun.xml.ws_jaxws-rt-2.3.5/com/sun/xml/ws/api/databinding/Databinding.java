/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import com.sun.xml.ws.api.databinding.ClientCallBridge;
import com.sun.xml.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.ws.api.message.MessageContextFactory;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.wsdl.DispatchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public interface Databinding
extends com.oracle.webservices.api.databinding.Databinding {
    public EndpointCallBridge getEndpointBridge(Packet var1) throws DispatchException;

    public ClientCallBridge getClientBridge(Method var1);

    public void generateWSDL(WSDLGenInfo var1);

    @Deprecated
    public ContentType encode(Packet var1, OutputStream var2) throws IOException;

    @Deprecated
    public void decode(InputStream var1, String var2, Packet var3) throws IOException;

    public MessageContextFactory getMessageContextFactory();
}

