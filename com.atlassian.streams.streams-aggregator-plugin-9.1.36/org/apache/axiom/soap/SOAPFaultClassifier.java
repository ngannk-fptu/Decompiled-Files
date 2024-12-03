/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;

public interface SOAPFaultClassifier
extends OMElement {
    public void setValue(SOAPFaultValue var1) throws SOAPProcessingException;

    public SOAPFaultValue getValue();

    public void setValue(QName var1);

    public QName getValueAsQName();

    public void setSubCode(SOAPFaultSubCode var1) throws SOAPProcessingException;

    public SOAPFaultSubCode getSubCode();
}

