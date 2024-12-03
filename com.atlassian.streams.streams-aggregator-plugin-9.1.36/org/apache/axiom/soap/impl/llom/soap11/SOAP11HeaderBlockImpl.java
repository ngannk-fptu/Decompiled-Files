/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderImpl;

public class SOAP11HeaderBlockImpl
extends SOAPHeaderBlockImpl {
    public SOAP11HeaderBlockImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
    }

    public SOAP11HeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAP11HeaderBlockImpl(String localName, OMNamespace ns, SOAPFactory factory, OMDataSource ds) {
        super(localName, ns, factory, ds);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11HeaderImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11HeaderImpl, got " + parent.getClass());
        }
    }

    public void setRole(String roleURI) {
        this.setAttribute("actor", roleURI, "http://schemas.xmlsoap.org/soap/envelope/");
    }

    public String getRole() {
        String val = this.hasOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.ROLE") ? this.getOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.ROLE") : this.getAttribute("actor", "http://schemas.xmlsoap.org/soap/envelope/");
        return val;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        this.setAttribute("mustUnderstand", mustUnderstand ? "1" : "0", "http://schemas.xmlsoap.org/soap/envelope/");
    }

    public void setMustUnderstand(String mustUnderstand) throws SOAPProcessingException {
        if (!("true".equals(mustUnderstand) || "false".equals(mustUnderstand) || "0".equals(mustUnderstand) || "1".equals(mustUnderstand))) {
            throw new SOAPProcessingException("mustUndertand should be one of \"true\", \"false\", \"0\" or \"1\" ");
        }
        this.setAttribute("mustUnderstand", mustUnderstand, "http://schemas.xmlsoap.org/soap/envelope/");
    }

    public boolean getMustUnderstand() throws SOAPProcessingException {
        String mustUnderstand = this.hasOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND") ? this.getOMDataSourceProperty("org.apache.axiom.soap.SOAPHeader.MUST_UNDERSTAND") : this.getAttribute("mustUnderstand", "http://schemas.xmlsoap.org/soap/envelope/");
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
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }

    public boolean getRelay() {
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }

    public SOAPVersion getVersion() {
        return SOAP11Version.getSingleton();
    }
}

