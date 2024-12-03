/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.ws.model.wsdl.AbstractObjectImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartDescriptorImpl
extends AbstractObjectImpl
implements WSDLPartDescriptor {
    private QName name;
    private WSDLDescriptorKind type;

    public WSDLPartDescriptorImpl(XMLStreamReader xsr, QName name, WSDLDescriptorKind kind) {
        super(xsr);
        this.name = name;
        this.type = kind;
    }

    @Override
    public QName name() {
        return this.name;
    }

    @Override
    public WSDLDescriptorKind type() {
        return this.type;
    }
}

