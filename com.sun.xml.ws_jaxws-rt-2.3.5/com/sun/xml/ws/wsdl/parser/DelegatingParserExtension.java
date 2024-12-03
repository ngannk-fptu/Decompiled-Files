/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtensionContext;
import javax.xml.stream.XMLStreamReader;

class DelegatingParserExtension
extends WSDLParserExtension {
    protected final WSDLParserExtension core;

    public DelegatingParserExtension(WSDLParserExtension core) {
        this.core = core;
    }

    @Override
    public void start(WSDLParserExtensionContext context) {
        this.core.start(context);
    }

    @Override
    public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
        this.core.serviceAttributes(service, reader);
    }

    @Override
    public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
        return this.core.serviceElements(service, reader);
    }

    @Override
    public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
        this.core.portAttributes(port, reader);
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
        return this.core.portElements(port, reader);
    }

    @Override
    public boolean portTypeOperationInput(EditableWSDLOperation op, XMLStreamReader reader) {
        return this.core.portTypeOperationInput(op, reader);
    }

    @Override
    public boolean portTypeOperationOutput(EditableWSDLOperation op, XMLStreamReader reader) {
        return this.core.portTypeOperationOutput(op, reader);
    }

    @Override
    public boolean portTypeOperationFault(EditableWSDLOperation op, XMLStreamReader reader) {
        return this.core.portTypeOperationFault(op, reader);
    }

    @Override
    public boolean definitionsElements(XMLStreamReader reader) {
        return this.core.definitionsElements(reader);
    }

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        return this.core.bindingElements(binding, reader);
    }

    @Override
    public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        this.core.bindingAttributes(binding, reader);
    }

    @Override
    public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
        return this.core.portTypeElements(portType, reader);
    }

    @Override
    public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
        this.core.portTypeAttributes(portType, reader);
    }

    @Override
    public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
        return this.core.portTypeOperationElements(operation, reader);
    }

    @Override
    public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
        this.core.portTypeOperationAttributes(operation, reader);
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return this.core.bindingOperationElements(operation, reader);
    }

    @Override
    public void bindingOperationAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        this.core.bindingOperationAttributes(operation, reader);
    }

    @Override
    public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
        return this.core.messageElements(msg, reader);
    }

    @Override
    public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
        this.core.messageAttributes(msg, reader);
    }

    @Override
    public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
        return this.core.portTypeOperationInputElements(input, reader);
    }

    @Override
    public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
        this.core.portTypeOperationInputAttributes(input, reader);
    }

    @Override
    public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
        return this.core.portTypeOperationOutputElements(output, reader);
    }

    @Override
    public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
        this.core.portTypeOperationOutputAttributes(output, reader);
    }

    @Override
    public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
        return this.core.portTypeOperationFaultElements(fault, reader);
    }

    @Override
    public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
        this.core.portTypeOperationFaultAttributes(fault, reader);
    }

    @Override
    public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return this.core.bindingOperationInputElements(operation, reader);
    }

    @Override
    public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        this.core.bindingOperationInputAttributes(operation, reader);
    }

    @Override
    public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return this.core.bindingOperationOutputElements(operation, reader);
    }

    @Override
    public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        this.core.bindingOperationOutputAttributes(operation, reader);
    }

    @Override
    public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        return this.core.bindingOperationFaultElements(fault, reader);
    }

    @Override
    public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        this.core.bindingOperationFaultAttributes(fault, reader);
    }

    @Override
    public void finished(WSDLParserExtensionContext context) {
        this.core.finished(context);
    }

    @Override
    public void postFinished(WSDLParserExtensionContext context) {
        this.core.postFinished(context);
    }
}

