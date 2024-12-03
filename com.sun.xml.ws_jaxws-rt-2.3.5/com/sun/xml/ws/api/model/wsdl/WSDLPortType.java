/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import javax.xml.namespace.QName;

public interface WSDLPortType
extends WSDLObject,
WSDLExtensible {
    public QName getName();

    public WSDLOperation get(String var1);

    public Iterable<? extends WSDLOperation> getOperations();
}

