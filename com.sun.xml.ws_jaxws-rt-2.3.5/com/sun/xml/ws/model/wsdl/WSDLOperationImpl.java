/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import com.sun.xml.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOperationImpl
extends AbstractExtensibleImpl
implements EditableWSDLOperation {
    private final QName name;
    private String parameterOrder;
    private EditableWSDLInput input;
    private EditableWSDLOutput output;
    private final List<EditableWSDLFault> faults;
    private final QNameMap<EditableWSDLFault> faultMap;
    protected Iterable<EditableWSDLMessage> messages;
    private final EditableWSDLPortType owner;

    public WSDLOperationImpl(XMLStreamReader xsr, EditableWSDLPortType owner, QName name) {
        super(xsr);
        this.name = name;
        this.faults = new ArrayList<EditableWSDLFault>();
        this.faultMap = new QNameMap();
        this.owner = owner;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    public String getParameterOrder() {
        return this.parameterOrder;
    }

    @Override
    public void setParameterOrder(String parameterOrder) {
        this.parameterOrder = parameterOrder;
    }

    @Override
    public EditableWSDLInput getInput() {
        return this.input;
    }

    @Override
    public void setInput(EditableWSDLInput input) {
        this.input = input;
    }

    @Override
    public EditableWSDLOutput getOutput() {
        return this.output;
    }

    @Override
    public boolean isOneWay() {
        return this.output == null;
    }

    @Override
    public void setOutput(EditableWSDLOutput output) {
        this.output = output;
    }

    public Iterable<EditableWSDLFault> getFaults() {
        return this.faults;
    }

    @Override
    public EditableWSDLFault getFault(QName faultDetailName) {
        EditableWSDLFault fault = this.faultMap.get(faultDetailName);
        if (fault != null) {
            return fault;
        }
        for (EditableWSDLFault fi : this.faults) {
            assert (fi.getMessage().parts().iterator().hasNext());
            EditableWSDLPart part = fi.getMessage().parts().iterator().next();
            if (!part.getDescriptor().name().equals(faultDetailName)) continue;
            this.faultMap.put(faultDetailName, fi);
            return fi;
        }
        return null;
    }

    @Override
    @NotNull
    public QName getPortTypeName() {
        return this.owner.getName();
    }

    @Override
    public void addFault(EditableWSDLFault fault) {
        this.faults.add(fault);
    }

    @Override
    public void freeze(EditableWSDLModel root) {
        assert (this.input != null);
        this.input.freeze(root);
        if (this.output != null) {
            this.output.freeze(root);
        }
        for (EditableWSDLFault fault : this.faults) {
            fault.freeze(root);
        }
    }
}

