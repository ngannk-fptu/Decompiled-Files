/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.wsdl.WSDLProperties;
import javax.xml.namespace.QName;

public final class WSDLPortProperties
extends WSDLProperties {
    @NotNull
    private final WSDLPort port;

    public WSDLPortProperties(@NotNull WSDLPort port) {
        this(port, null);
    }

    public WSDLPortProperties(@NotNull WSDLPort port, @Nullable SEIModel seiModel) {
        super(seiModel);
        this.port = port;
    }

    @Override
    public QName getWSDLService() {
        return this.port.getOwner().getName();
    }

    @Override
    public QName getWSDLPort() {
        return this.port.getName();
    }

    @Override
    public QName getWSDLPortType() {
        return this.port.getBinding().getPortTypeName();
    }
}

