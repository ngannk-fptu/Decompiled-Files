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
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import javax.xml.namespace.QName;

public interface WSDLService
extends WSDLObject,
WSDLExtensible {
    @NotNull
    public WSDLModel getParent();

    @NotNull
    public QName getName();

    public WSDLPort get(QName var1);

    public WSDLPort getFirstPort();

    @Nullable
    public WSDLPort getMatchingPort(QName var1);

    public Iterable<? extends WSDLPort> getPorts();
}

