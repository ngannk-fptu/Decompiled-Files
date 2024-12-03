/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;

public class SOAP12FaultRoleImpl
extends SOAPFaultRoleImpl {
    public SOAP12FaultRoleImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, true, factory);
    }

    public SOAP12FaultRoleImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultRoleImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl, got " + parent.getClass());
        }
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.registerContentHandler(writer);
        super.internalSerialize(writer, cache);
    }
}

