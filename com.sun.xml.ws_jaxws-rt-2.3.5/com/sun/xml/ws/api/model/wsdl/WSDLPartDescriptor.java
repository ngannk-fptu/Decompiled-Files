/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import javax.xml.namespace.QName;

public interface WSDLPartDescriptor
extends WSDLObject {
    public QName name();

    public WSDLDescriptorKind type();
}

