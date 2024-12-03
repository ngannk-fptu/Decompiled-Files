/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;

public class SOAP11FaultRoleImpl
extends SOAPFaultRoleImpl {
    public SOAP11FaultRoleImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, false, factory);
    }

    public SOAP11FaultRoleImpl(SOAPFactory factory) {
        super(null, factory);
    }

    public SOAP11FaultRoleImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.registerContentHandler(writer);
        OMSerializerUtil.serializeStartpart(this, "faultactor", writer);
        writer.writeCharacters(this.getText());
        writer.writeEndElement();
    }
}

