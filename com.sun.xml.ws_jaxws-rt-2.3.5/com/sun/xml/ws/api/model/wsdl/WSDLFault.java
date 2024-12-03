/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import javax.xml.namespace.QName;

public interface WSDLFault
extends WSDLObject,
WSDLExtensible {
    public String getName();

    public WSDLMessage getMessage();

    @NotNull
    public WSDLOperation getOperation();

    @NotNull
    public QName getQName();

    public String getAction();

    public boolean isDefaultAction();
}

