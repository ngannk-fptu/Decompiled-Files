/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.model.wsdl.WSDLProperties;
import javax.xml.namespace.QName;

public final class WSDLDirectProperties
extends WSDLProperties {
    private final QName serviceName;
    private final QName portName;

    public WSDLDirectProperties(QName serviceName, QName portName) {
        this(serviceName, portName, null);
    }

    public WSDLDirectProperties(QName serviceName, QName portName, SEIModel seiModel) {
        super(seiModel);
        this.serviceName = serviceName;
        this.portName = portName;
    }

    @Override
    public QName getWSDLService() {
        return this.serviceName;
    }

    @Override
    public QName getWSDLPort() {
        return this.portName;
    }

    @Override
    public QName getWSDLPortType() {
        return null;
    }
}

