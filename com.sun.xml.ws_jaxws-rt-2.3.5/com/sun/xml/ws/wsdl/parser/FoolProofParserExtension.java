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
import com.sun.xml.ws.wsdl.parser.DelegatingParserExtension;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

final class FoolProofParserExtension
extends DelegatingParserExtension {
    public FoolProofParserExtension(WSDLParserExtension core) {
        super(core);
    }

    private QName pre(XMLStreamReader xsr) {
        return xsr.getName();
    }

    private boolean post(QName tagName, XMLStreamReader xsr, boolean result) {
        if (!tagName.equals(xsr.getName())) {
            return this.foundFool();
        }
        if (result) {
            if (xsr.getEventType() != 2) {
                this.foundFool();
            }
        } else if (xsr.getEventType() != 1) {
            this.foundFool();
        }
        return result;
    }

    private boolean foundFool() {
        throw new AssertionError((Object)("XMLStreamReader is placed at the wrong place after invoking " + this.core));
    }

    @Override
    public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.serviceElements(service, reader));
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portElements(port, reader));
    }

    @Override
    public boolean definitionsElements(XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.definitionsElements(reader));
    }

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingElements(binding, reader));
    }

    @Override
    public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeElements(portType, reader));
    }

    @Override
    public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationElements(operation, reader));
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationElements(operation, reader));
    }

    @Override
    public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.messageElements(msg, reader));
    }

    @Override
    public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationInputElements(input, reader));
    }

    @Override
    public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationOutputElements(output, reader));
    }

    @Override
    public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.portTypeOperationFaultElements(fault, reader));
    }

    @Override
    public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return super.bindingOperationInputElements(operation, reader);
    }

    @Override
    public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationOutputElements(operation, reader));
    }

    @Override
    public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        return this.post(this.pre(reader), reader, super.bindingOperationFaultElements(fault, reader));
    }
}

