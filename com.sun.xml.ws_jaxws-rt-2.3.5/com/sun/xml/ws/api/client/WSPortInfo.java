/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.xml.ws.handler.PortInfo
 */
package com.sun.xml.ws.api.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.PolicyMap;
import javax.xml.ws.handler.PortInfo;

public interface WSPortInfo
extends PortInfo {
    @NotNull
    public WSService getOwner();

    @NotNull
    public BindingID getBindingId();

    @NotNull
    public EndpointAddress getEndpointAddress();

    @Nullable
    public WSDLPort getPort();

    public PolicyMap getPolicyMap();
}

