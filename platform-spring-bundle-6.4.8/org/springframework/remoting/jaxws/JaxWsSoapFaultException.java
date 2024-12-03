/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.soap.SOAPFaultException
 */
package org.springframework.remoting.jaxws;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import org.springframework.remoting.soap.SoapFaultException;

public class JaxWsSoapFaultException
extends SoapFaultException {
    public JaxWsSoapFaultException(SOAPFaultException original) {
        super(original.getMessage(), original);
    }

    public final SOAPFault getFault() {
        return ((SOAPFaultException)this.getCause()).getFault();
    }

    @Override
    public String getFaultCode() {
        return this.getFault().getFaultCode();
    }

    @Override
    public QName getFaultCodeAsQName() {
        return this.getFault().getFaultCodeAsQName();
    }

    @Override
    public String getFaultString() {
        return this.getFault().getFaultString();
    }

    @Override
    public String getFaultActor() {
        return this.getFault().getFaultActor();
    }
}

