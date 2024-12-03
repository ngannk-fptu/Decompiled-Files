/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import java.util.List;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;

public interface SOAPFaultReason
extends OMElement {
    public void addSOAPText(SOAPFaultText var1) throws SOAPProcessingException;

    public SOAPFaultText getFirstSOAPText();

    public List getAllSoapTexts();

    public SOAPFaultText getSOAPFaultText(String var1);
}

