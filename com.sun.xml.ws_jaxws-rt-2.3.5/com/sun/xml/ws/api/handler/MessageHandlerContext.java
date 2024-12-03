/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.api.handler;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public interface MessageHandlerContext
extends MessageContext {
    public Message getMessage();

    public void setMessage(Message var1);

    public Set<String> getRoles();

    public WSBinding getWSBinding();

    @Nullable
    public SEIModel getSEIModel();

    @Nullable
    public WSDLPort getPort();
}

