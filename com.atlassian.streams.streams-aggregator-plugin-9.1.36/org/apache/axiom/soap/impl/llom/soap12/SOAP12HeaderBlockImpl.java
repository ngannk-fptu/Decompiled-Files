/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12HeaderImpl;

public class SOAP12HeaderBlockImpl
extends SOAPHeaderBlockImpl
implements SOAP12Constants {
    public SOAP12HeaderBlockImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
    }

    public SOAP12HeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAP12HeaderBlockImpl(String localName, OMNamespace ns, SOAPFactory factory, OMDataSource ds) {
        super(localName, ns, factory, ds);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12HeaderImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12HeaderImpl as parent, got " + parent.getClass());
        }
    }

    public void setRole(String roleURI) {
        this.setAttribute("role", roleURI, "http://www.w3.org/2003/05/soap-envelope");
    }

    public String getRole() {
        String val = this.hasOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.ROLE") ? this.getOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.ROLE") : this.getAttributeValue(QNAME_ROLE);
        return val;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        this.setAttribute("mustUnderstand", mustUnderstand ? "true" : "false", "http://www.w3.org/2003/05/soap-envelope");
    }

    public void setMustUnderstand(String mustUnderstand) throws SOAPProcessingException {
        if (!("true".equals(mustUnderstand) || "false".equals(mustUnderstand) || "0".equals(mustUnderstand) || "1".equals(mustUnderstand))) {
            throw new SOAPProcessingException("mustUndertand should be one of \"true\", \"false\", \"0\" or \"1\" ");
        }
        this.setAttribute("mustUnderstand", mustUnderstand, "http://www.w3.org/2003/05/soap-envelope");
    }

    public boolean getMustUnderstand() throws SOAPProcessingException {
        String mustUnderstand = this.hasOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND") ? this.getOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND") : this.getAttribute("mustUnderstand", "http://www.w3.org/2003/05/soap-envelope");
        if (mustUnderstand != null) {
            if ("true".equals(mustUnderstand) || "1".equals(mustUnderstand)) {
                return true;
            }
            if ("false".equals(mustUnderstand) || "0".equals(mustUnderstand)) {
                return false;
            }
            throw new SOAPProcessingException("Invalid value found in mustUnderstand value of " + this.getLocalName() + " header block");
        }
        return false;
    }

    public void setRelay(boolean relay) {
        this.setAttribute("relay", relay ? "true" : "false", "http://www.w3.org/2003/05/soap-envelope");
    }

    public boolean getRelay() {
        boolean ret = false;
        String val = this.hasOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.RELAY") ? this.getOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.RELAY") : this.getAttributeValue(QNAME_RELAY);
        if (val != null) {
            ret = "true".equalsIgnoreCase(val);
        }
        return ret;
    }

    public SOAPVersion getVersion() {
        return SOAP12Version.getSingleton();
    }
}

