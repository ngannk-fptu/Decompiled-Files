/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.AddressingFeature
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.wsdl.parser.ParserUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLParserExtension
extends WSDLParserExtension {
    protected static final String COLON_DELIMITER = ":";
    protected static final String SLASH_DELIMITER = "/";

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        return this.addressibleElement(reader, binding);
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
        return this.addressibleElement(reader, port);
    }

    private boolean addressibleElement(XMLStreamReader reader, WSDLFeaturedObject binding) {
        QName ua = reader.getName();
        if (ua.equals(AddressingVersion.W3C.wsdlExtensionTag)) {
            String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
            binding.addFeature((WebServiceFeature)new AddressingFeature(true, Boolean.parseBoolean(required)));
            XMLStreamReaderUtil.skipElement(reader);
            return true;
        }
        return false;
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        EditableWSDLBoundOperation edit = operation;
        QName anon = reader.getName();
        if (anon.equals(AddressingVersion.W3C.wsdlAnonymousTag)) {
            block7: {
                try {
                    String value = reader.getElementText();
                    if (value == null || value.trim().equals("")) {
                        throw new WebServiceException("Null values not permitted in wsaw:Anonymous.");
                    }
                    if (value.equals("optional")) {
                        edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
                        break block7;
                    }
                    if (value.equals("required")) {
                        edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.required);
                        break block7;
                    }
                    if (value.equals("prohibited")) {
                        edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.prohibited);
                        break block7;
                    }
                    throw new WebServiceException("wsaw:Anonymous value \"" + value + "\" not understood.");
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
        String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            input.setAction(action);
            input.setDefaultAction(false);
        }
    }

    @Override
    public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
        String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            output.setAction(action);
            output.setDefaultAction(false);
        }
    }

    @Override
    public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
        String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
        if (action != null) {
            fault.setAction(action);
            fault.setDefaultAction(false);
        }
    }

    @Override
    public void finished(WSDLParserExtensionContext context) {
        EditableWSDLModel model = context.getWSDLModel();
        for (EditableWSDLService editableWSDLService : model.getServices().values()) {
            for (EditableWSDLPort editableWSDLPort : editableWSDLService.getPorts()) {
                EditableWSDLBoundPortType binding = editableWSDLPort.getBinding();
                this.populateActions(binding);
                this.patchAnonymousDefault(binding);
            }
        }
    }

    protected String getNamespaceURI() {
        return AddressingVersion.W3C.wsdlNsUri;
    }

    protected QName getWsdlActionTag() {
        return AddressingVersion.W3C.wsdlActionTag;
    }

    private void populateActions(EditableWSDLBoundPortType binding) {
        EditableWSDLPortType porttype = binding.getPortType();
        for (EditableWSDLOperation editableWSDLOperation : porttype.getOperations()) {
            EditableWSDLBoundOperation wboi = binding.get(editableWSDLOperation.getName());
            if (wboi == null) {
                editableWSDLOperation.getInput().setAction(this.defaultInputAction(editableWSDLOperation));
                continue;
            }
            String soapAction = wboi.getSOAPAction();
            if (editableWSDLOperation.getInput().getAction() == null || editableWSDLOperation.getInput().getAction().equals("")) {
                if (soapAction != null && !soapAction.equals("")) {
                    editableWSDLOperation.getInput().setAction(soapAction);
                } else {
                    editableWSDLOperation.getInput().setAction(this.defaultInputAction(editableWSDLOperation));
                }
            }
            if (editableWSDLOperation.getOutput() == null) continue;
            if (editableWSDLOperation.getOutput().getAction() == null || editableWSDLOperation.getOutput().getAction().equals("")) {
                editableWSDLOperation.getOutput().setAction(this.defaultOutputAction(editableWSDLOperation));
            }
            if (editableWSDLOperation.getFaults() == null || !editableWSDLOperation.getFaults().iterator().hasNext()) continue;
            for (EditableWSDLFault editableWSDLFault : editableWSDLOperation.getFaults()) {
                if (editableWSDLFault.getAction() != null && !editableWSDLFault.getAction().equals("")) continue;
                editableWSDLFault.setAction(this.defaultFaultAction(editableWSDLFault.getName(), editableWSDLOperation));
            }
        }
    }

    protected void patchAnonymousDefault(EditableWSDLBoundPortType binding) {
        for (EditableWSDLBoundOperation editableWSDLBoundOperation : binding.getBindingOperations()) {
            if (editableWSDLBoundOperation.getAnonymous() != null) continue;
            editableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
        }
    }

    private String defaultInputAction(EditableWSDLOperation o) {
        return W3CAddressingWSDLParserExtension.buildAction(o.getInput().getName(), o, false);
    }

    private String defaultOutputAction(EditableWSDLOperation o) {
        return W3CAddressingWSDLParserExtension.buildAction(o.getOutput().getName(), o, false);
    }

    private String defaultFaultAction(String name, EditableWSDLOperation o) {
        return W3CAddressingWSDLParserExtension.buildAction(name, o, true);
    }

    protected static final String buildAction(String name, EditableWSDLOperation o, boolean isFault) {
        String tns = o.getName().getNamespaceURI();
        String delim = SLASH_DELIMITER;
        if (!tns.startsWith("http")) {
            delim = COLON_DELIMITER;
        }
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        if (o.getPortTypeName() == null) {
            throw new WebServiceException("\"" + o.getName() + "\" operation's owning portType name is null.");
        }
        return tns + delim + o.getPortTypeName().getLocalPart() + delim + (isFault ? o.getName().getLocalPart() + delim + "Fault" + delim : "") + name;
    }
}

