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
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import javax.xml.namespace.QName;

public interface WSDLBoundFault
extends WSDLObject,
WSDLExtensible {
    @NotNull
    public String getName();

    @Nullable
    public QName getQName();

    @Nullable
    public WSDLFault getFault();

    @NotNull
    public WSDLBoundOperation getBoundOperation();
}

