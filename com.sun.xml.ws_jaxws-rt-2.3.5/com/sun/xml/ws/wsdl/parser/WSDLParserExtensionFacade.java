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
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

final class WSDLParserExtensionFacade
extends WSDLParserExtension {
    private final WSDLParserExtension[] extensions;

    WSDLParserExtensionFacade(WSDLParserExtension ... extensions) {
        assert (extensions != null);
        this.extensions = extensions;
    }

    @Override
    public void start(WSDLParserExtensionContext context) {
        for (WSDLParserExtension e : this.extensions) {
            e.start(context);
        }
    }

    @Override
    public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.serviceElements(service, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.serviceAttributes(service, reader);
        }
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portElements(port, reader)) continue;
            return true;
        }
        if (this.isRequiredExtension(reader)) {
            port.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public boolean portTypeOperationInput(EditableWSDLOperation op, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationInput(op, reader);
        }
        return false;
    }

    @Override
    public boolean portTypeOperationOutput(EditableWSDLOperation op, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationOutput(op, reader);
        }
        return false;
    }

    @Override
    public boolean portTypeOperationFault(EditableWSDLOperation op, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationFault(op, reader);
        }
        return false;
    }

    @Override
    public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portAttributes(port, reader);
        }
    }

    @Override
    public boolean definitionsElements(XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.definitionsElements(reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.bindingElements(binding, reader)) continue;
            return true;
        }
        if (this.isRequiredExtension(reader)) {
            binding.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.bindingAttributes(binding, reader);
        }
    }

    @Override
    public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portTypeElements(portType, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeAttributes(portType, reader);
        }
    }

    @Override
    public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portTypeOperationElements(operation, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationAttributes(operation, reader);
        }
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.bindingOperationElements(operation, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void bindingOperationAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.bindingOperationAttributes(operation, reader);
        }
    }

    @Override
    public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.messageElements(msg, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.messageAttributes(msg, reader);
        }
    }

    @Override
    public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portTypeOperationInputElements(input, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationInputAttributes(input, reader);
        }
    }

    @Override
    public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portTypeOperationOutputElements(output, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationOutputAttributes(output, reader);
        }
    }

    @Override
    public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.portTypeOperationFaultElements(fault, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.portTypeOperationFaultAttributes(fault, reader);
        }
    }

    @Override
    public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.bindingOperationInputElements(operation, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.bindingOperationInputAttributes(operation, reader);
        }
    }

    @Override
    public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.bindingOperationOutputElements(operation, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.bindingOperationOutputAttributes(operation, reader);
        }
    }

    @Override
    public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            if (!e.bindingOperationFaultElements(fault, reader)) continue;
            return true;
        }
        XMLStreamReaderUtil.skipElement(reader);
        return true;
    }

    @Override
    public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        for (WSDLParserExtension e : this.extensions) {
            e.bindingOperationFaultAttributes(fault, reader);
        }
    }

    @Override
    public void finished(WSDLParserExtensionContext context) {
        for (WSDLParserExtension e : this.extensions) {
            e.finished(context);
        }
    }

    @Override
    public void postFinished(WSDLParserExtensionContext context) {
        for (WSDLParserExtension e : this.extensions) {
            e.postFinished(context);
        }
    }

    private boolean isRequiredExtension(XMLStreamReader reader) {
        String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
        if (required != null) {
            return Boolean.parseBoolean(required);
        }
        return false;
    }

    private Locator getLocator(XMLStreamReader reader) {
        Location location = reader.getLocation();
        LocatorImpl loc = new LocatorImpl();
        loc.setSystemId(location.getSystemId());
        loc.setLineNumber(location.getLineNumber());
        return loc;
    }
}

