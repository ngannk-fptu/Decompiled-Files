/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPProcessingException;

public interface SOAPMessage
extends OMDocument {
    public SOAPEnvelope getSOAPEnvelope() throws SOAPProcessingException;

    public void setSOAPEnvelope(SOAPEnvelope var1) throws SOAPProcessingException;
}

