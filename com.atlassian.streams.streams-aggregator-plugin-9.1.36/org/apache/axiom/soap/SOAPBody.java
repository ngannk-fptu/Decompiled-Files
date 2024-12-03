/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFault;

public interface SOAPBody
extends OMElement {
    public SOAPFault addFault(Exception var1) throws OMException;

    public boolean hasFault();

    public SOAPFault getFault();

    public void addFault(SOAPFault var1) throws OMException;

    public OMNamespace getFirstElementNS();

    public String getFirstElementLocalName();
}

