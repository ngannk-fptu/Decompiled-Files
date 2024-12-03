/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.builder;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.SOAPBuilderHelper;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.w3c.dom.Element;

public class SOAP11BuilderHelper
extends SOAPBuilderHelper
implements SOAP11Constants {
    private boolean faultcodePresent = false;
    private boolean faultstringPresent = false;

    public SOAP11BuilderHelper(StAXSOAPModelBuilder builder, SOAPFactoryEx factory) {
        super(builder, factory);
    }

    public OMElement handleEvent(XMLStreamReader parser, OMElement parent, int elementLevel) throws SOAPProcessingException {
        this.parser = parser;
        OMElement element = null;
        String localName = parser.getLocalName();
        if (elementLevel == 4) {
            if ("faultcode".equals(localName)) {
                element = this.factory.createSOAPFaultCode((SOAPFault)parent, this.builder);
                this.faultcodePresent = true;
            } else if ("faultstring".equals(localName)) {
                element = this.factory.createSOAPFaultReason((SOAPFault)parent, this.builder);
                this.faultstringPresent = true;
            } else {
                element = "faultactor".equals(localName) ? this.factory.createSOAPFaultRole((SOAPFault)parent, this.builder) : ("detail".equals(localName) ? this.factory.createSOAPFaultDetail((SOAPFault)parent, this.builder) : this.factory.createOMElement(localName, parent, this.builder));
            }
        } else if (elementLevel == 5) {
            String parentTagName = "";
            parentTagName = parent instanceof Element ? ((Element)((Object)parent)).getTagName() : parent.getLocalName();
            if (parentTagName.equals("faultcode")) {
                throw new SOAPProcessingException("faultcode element should not have children");
            }
            if (parentTagName.equals("faultstring")) {
                throw new SOAPProcessingException("faultstring element should not have children");
            }
            if (parentTagName.equals("faultactor")) {
                throw new SOAPProcessingException("faultactor element should not have children");
            }
            element = this.factory.createOMElement(localName, parent, this.builder);
        } else if (elementLevel > 5) {
            element = this.factory.createOMElement(localName, parent, this.builder);
        }
        return element;
    }
}

