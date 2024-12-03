/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface EditableWSDLBoundPortType
extends WSDLBoundPortType {
    @Override
    @NotNull
    public EditableWSDLModel getOwner();

    @Override
    public EditableWSDLBoundOperation get(QName var1);

    @Override
    public EditableWSDLPortType getPortType();

    public Iterable<? extends EditableWSDLBoundOperation> getBindingOperations();

    @Override
    @Nullable
    public EditableWSDLBoundOperation getOperation(String var1, String var2);

    public void put(QName var1, EditableWSDLBoundOperation var2);

    public void setBindingId(BindingID var1);

    public void setStyle(SOAPBinding.Style var1);

    public void freeze();
}

