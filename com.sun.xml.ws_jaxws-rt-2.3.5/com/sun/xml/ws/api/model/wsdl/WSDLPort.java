/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import javax.xml.namespace.QName;

public interface WSDLPort
extends WSDLFeaturedObject,
WSDLExtensible {
    public QName getName();

    @NotNull
    public WSDLBoundPortType getBinding();

    public EndpointAddress getAddress();

    @NotNull
    public WSDLService getOwner();

    @Nullable
    public WSEndpointReference getEPR();
}

