/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;

public interface EditableWSDLPort
extends WSDLPort {
    @Override
    @NotNull
    public EditableWSDLBoundPortType getBinding();

    @Override
    @NotNull
    public EditableWSDLService getOwner();

    public void setAddress(EndpointAddress var1);

    public void setEPR(@NotNull WSEndpointReference var1);

    public void freeze(EditableWSDLModel var1);
}

