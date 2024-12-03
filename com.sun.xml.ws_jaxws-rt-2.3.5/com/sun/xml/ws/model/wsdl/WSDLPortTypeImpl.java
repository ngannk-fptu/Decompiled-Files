/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPortTypeImpl
extends AbstractExtensibleImpl
implements EditableWSDLPortType {
    private QName name;
    private final Map<String, EditableWSDLOperation> portTypeOperations;
    private EditableWSDLModel owner;

    public WSDLPortTypeImpl(XMLStreamReader xsr, EditableWSDLModel owner, QName name) {
        super(xsr);
        this.name = name;
        this.owner = owner;
        this.portTypeOperations = new Hashtable<String, EditableWSDLOperation>();
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    public EditableWSDLOperation get(String operationName) {
        return this.portTypeOperations.get(operationName);
    }

    public Iterable<EditableWSDLOperation> getOperations() {
        return this.portTypeOperations.values();
    }

    @Override
    public void put(String opName, EditableWSDLOperation ptOp) {
        this.portTypeOperations.put(opName, ptOp);
    }

    EditableWSDLModel getOwner() {
        return this.owner;
    }

    @Override
    public void freeze() {
        for (EditableWSDLOperation op : this.portTypeOperations.values()) {
            op.freeze(this.owner);
        }
    }
}

