/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 */
package com.sun.xml.messaging.saaj.soap.ver1_2;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.SOAPFactoryImpl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

public class SOAPFactory1_2Impl
extends SOAPFactoryImpl {
    @Override
    protected SOAPDocumentImpl createDocument() {
        return new SOAPPart1_2Impl().getDocument();
    }

    @Override
    public Detail createDetail() throws SOAPException {
        return new Detail1_2Impl(this.createDocument());
    }

    @Override
    public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException {
        if (faultCode == null) {
            throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
        }
        if (reasonText == null) {
            throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
        }
        Fault1_2Impl fault = new Fault1_2Impl(this.createDocument());
        fault.setFaultCode(faultCode);
        fault.setFaultString(reasonText);
        return fault;
    }

    @Override
    public SOAPFault createFault() throws SOAPException {
        Fault1_2Impl fault = new Fault1_2Impl(this.createDocument());
        fault.setFaultCode(fault.getDefaultFaultCode());
        fault.setFaultString("Fault string, and possibly fault code, not set");
        return fault;
    }
}

