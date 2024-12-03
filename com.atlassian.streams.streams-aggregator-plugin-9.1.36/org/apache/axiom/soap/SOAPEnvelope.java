/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPVersion;

public interface SOAPEnvelope
extends OMElement {
    public SOAPHeader getHeader() throws OMException;

    public SOAPBody getBody() throws OMException;

    public SOAPVersion getVersion();

    public boolean hasFault();

    public OMNamespace getSOAPBodyFirstElementNS();

    public String getSOAPBodyFirstElementLocalName();
}

