/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class WSDLBoundFaultImpl
extends AbstractExtensibleImpl
implements EditableWSDLBoundFault {
    private final String name;
    private EditableWSDLFault fault;
    private EditableWSDLBoundOperation owner;

    public WSDLBoundFaultImpl(XMLStreamReader xsr, String name, EditableWSDLBoundOperation owner) {
        super(xsr);
        this.name = name;
        this.owner = owner;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    @Override
    public QName getQName() {
        if (this.owner.getOperation() != null) {
            return new QName(this.owner.getOperation().getName().getNamespaceURI(), this.name);
        }
        return null;
    }

    @Override
    public EditableWSDLFault getFault() {
        return this.fault;
    }

    @Override
    @NotNull
    public EditableWSDLBoundOperation getBoundOperation() {
        return this.owner;
    }

    @Override
    public void freeze(EditableWSDLBoundOperation root) {
        assert (root != null);
        EditableWSDLOperation op = root.getOperation();
        if (op != null) {
            for (EditableWSDLFault editableWSDLFault : op.getFaults()) {
                if (!editableWSDLFault.getName().equals(this.name)) continue;
                this.fault = editableWSDLFault;
                break;
            }
        }
    }
}

