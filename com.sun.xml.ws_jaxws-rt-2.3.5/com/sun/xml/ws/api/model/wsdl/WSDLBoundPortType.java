/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.WebParam$Mode
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPortType;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface WSDLBoundPortType
extends WSDLFeaturedObject,
WSDLExtensible {
    public QName getName();

    @NotNull
    public WSDLModel getOwner();

    public WSDLBoundOperation get(QName var1);

    public QName getPortTypeName();

    public WSDLPortType getPortType();

    public Iterable<? extends WSDLBoundOperation> getBindingOperations();

    @NotNull
    public SOAPBinding.Style getStyle();

    public BindingID getBindingId();

    @Nullable
    public WSDLBoundOperation getOperation(String var1, String var2);

    public ParameterBinding getBinding(QName var1, String var2, WebParam.Mode var3);
}

