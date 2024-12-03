/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.wsdl.parser.W3CAddressingWSDLParserExtension;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class MemberSubmissionAddressingWSDLParserExtension
extends W3CAddressingWSDLParserExtension {
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
        if (ua.equals(AddressingVersion.MEMBER.wsdlExtensionTag)) {
            String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
            binding.addFeature(new MemberSubmissionAddressingFeature(Boolean.parseBoolean(required)));
            XMLStreamReaderUtil.skipElement(reader);
            return true;
        }
        return false;
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        return false;
    }

    @Override
    protected void patchAnonymousDefault(EditableWSDLBoundPortType binding) {
    }

    @Override
    protected String getNamespaceURI() {
        return AddressingVersion.MEMBER.wsdlNsUri;
    }

    @Override
    protected QName getWsdlActionTag() {
        return AddressingVersion.MEMBER.wsdlActionTag;
    }
}

