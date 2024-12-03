/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.BindingProvider
 *  org.glassfish.gmbal.ManagedObjectManager
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.ComponentRegistry;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.Header;
import java.io.Closeable;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.glassfish.gmbal.ManagedObjectManager;

public interface WSBindingProvider
extends BindingProvider,
Closeable,
ComponentRegistry {
    public void setOutboundHeaders(List<Header> var1);

    public void setOutboundHeaders(Header ... var1);

    public void setOutboundHeaders(Object ... var1);

    public List<Header> getInboundHeaders();

    public void setAddress(String var1);

    public WSEndpointReference getWSEndpointReference();

    public WSPortInfo getPortInfo();

    @NotNull
    public ManagedObjectManager getManagedObjectManager();
}

