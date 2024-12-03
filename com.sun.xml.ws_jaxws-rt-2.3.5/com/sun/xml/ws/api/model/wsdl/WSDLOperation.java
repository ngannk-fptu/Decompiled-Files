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
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.WSDLOutput;
import javax.xml.namespace.QName;

public interface WSDLOperation
extends WSDLObject,
WSDLExtensible {
    @NotNull
    public QName getName();

    @NotNull
    public WSDLInput getInput();

    @Nullable
    public WSDLOutput getOutput();

    public boolean isOneWay();

    public Iterable<? extends WSDLFault> getFaults();

    @Nullable
    public WSDLFault getFault(QName var1);

    @NotNull
    public QName getPortTypeName();

    public String getParameterOrder();
}

