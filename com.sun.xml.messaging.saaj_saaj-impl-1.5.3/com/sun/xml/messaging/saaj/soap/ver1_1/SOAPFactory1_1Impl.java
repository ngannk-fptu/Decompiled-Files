/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.SOAPFactoryImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

public class SOAPFactory1_1Impl
extends SOAPFactoryImpl {
    @Override
    protected SOAPDocumentImpl createDocument() {
        return new SOAPPart1_1Impl().getDocument();
    }

    @Override
    public Detail createDetail() throws SOAPException {
        return new Detail1_1Impl(this.createDocument());
    }

    @Override
    public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException {
        if (faultCode == null) {
            throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
        }
        if (reasonText == null) {
            throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
        }
        Fault1_1Impl fault = new Fault1_1Impl(this.createDocument());
        fault.setFaultCode(faultCode);
        fault.setFaultString(reasonText);
        return fault;
    }

    @Override
    public SOAPFault createFault() throws SOAPException {
        Fault1_1Impl fault = new Fault1_1Impl(this.createDocument());
        fault.setFaultCode(fault.getDefaultFaultCode());
        fault.setFaultString("Fault string, and possibly fault code, not set");
        return fault;
    }
}

