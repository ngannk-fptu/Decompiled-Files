/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOutputImpl
extends AbstractExtensibleImpl
implements EditableWSDLOutput {
    private String name;
    private QName messageName;
    private EditableWSDLOperation operation;
    private EditableWSDLMessage message;
    private String action;
    private boolean defaultAction = true;

    public WSDLOutputImpl(XMLStreamReader xsr, String name, QName messageName, EditableWSDLOperation operation) {
        super(xsr);
        this.name = name;
        this.messageName = messageName;
        this.operation = operation;
    }

    @Override
    public String getName() {
        return this.name == null ? this.operation.getName().getLocalPart() + "Response" : this.name;
    }

    @Override
    public EditableWSDLMessage getMessage() {
        return this.message;
    }

    @Override
    public String getAction() {
        return this.action;
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
    @NotNull
    public EditableWSDLOperation getOperation() {
        return this.operation;
    }

    @Override
    @NotNull
    public QName getQName() {
        return new QName(this.operation.getName().getNamespaceURI(), this.getName());
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void freeze(EditableWSDLModel root) {
        this.message = root.getMessage(this.messageName);
    }
}

