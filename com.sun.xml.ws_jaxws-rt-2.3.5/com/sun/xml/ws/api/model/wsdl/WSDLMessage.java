/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLPart;
import javax.xml.namespace.QName;

public interface WSDLMessage
extends WSDLObject,
WSDLExtensible {
    public QName getName();

    public Iterable<? extends WSDLPart> parts();
}

