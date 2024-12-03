/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.wsdl.parser.W3CAddressingWSDLParserExtension;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class W3CAddressingMetadataWSDLParserExtension
extends W3CAddressingWSDLParserExtension {
    String METADATA_WSDL_EXTN_NS = "http://www.w3.org/2007/05/addressing/metadata";
    QName METADATA_WSDL_ACTION_TAG = new QName(this.METADATA_WSDL_EXTN_NS, "Action", "wsam");

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        return false;
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
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
        return this.METADATA_WSDL_EXTN_NS;
    }

    @Override
    protected QName getWsdlActionTag() {
        return this.METADATA_WSDL_ACTION_TAG;
    }
}

