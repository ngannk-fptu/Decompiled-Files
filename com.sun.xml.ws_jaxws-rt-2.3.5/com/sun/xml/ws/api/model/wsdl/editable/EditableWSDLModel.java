/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.ws.policy.PolicyMap
 */
package com.sun.xml.ws.api.model.wsdl.editable;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.policy.PolicyMap;
import java.util.Map;
import javax.xml.namespace.QName;

public interface EditableWSDLModel
extends WSDLModel {
    @Override
    public EditableWSDLPortType getPortType(@NotNull QName var1);

    public void addBinding(EditableWSDLBoundPortType var1);

    @Override
    public EditableWSDLBoundPortType getBinding(@NotNull QName var1);

    @Override
    public EditableWSDLBoundPortType getBinding(@NotNull QName var1, @NotNull QName var2);

    @Override
    public EditableWSDLService getService(@NotNull QName var1);

    @NotNull
    public Map<QName, ? extends EditableWSDLMessage> getMessages();

    public void addMessage(EditableWSDLMessage var1);

    @NotNull
    public Map<QName, ? extends EditableWSDLPortType> getPortTypes();

    public void addPortType(EditableWSDLPortType var1);

    @NotNull
    public Map<QName, ? extends EditableWSDLBoundPortType> getBindings();

    @NotNull
    public Map<QName, ? extends EditableWSDLService> getServices();

    public void addService(EditableWSDLService var1);

    @Override
    public EditableWSDLMessage getMessage(QName var1);

    public void setPolicyMap(PolicyMap var1);

    public void finalizeRpcLitBinding(EditableWSDLBoundPortType var1);

    public void freeze();
}

