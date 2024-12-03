/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLFaultImpl
extends AbstractExtensibleImpl
implements EditableWSDLFault {
    private final String name;
    private final QName messageName;
    private EditableWSDLMessage message;
    private EditableWSDLOperation operation;
    private String action = "";
    private boolean defaultAction = true;

    public WSDLFaultImpl(XMLStreamReader xsr, String name, QName messageName, EditableWSDLOperation operation) {
        super(xsr);
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EditableWSDLMessage getMessage() {
        return this.message;
    }

    @Override
    @NotNull
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }

    @Override
    @NotNull
    public QName getQName() {
        return new QName(this.operation.getName().getNamespaceURI(), this.name);
    }

    @Override
    @NotNull
    public String getAction() {
        return this.action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean isDefaultAction() {
        return this.defaultAction;
    }

    @Override
    public void setDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
    }

    @Override
    public void freeze(EditableWSDLModel root) {
        this.message = root.getMessage(this.messageName);
    }
}

