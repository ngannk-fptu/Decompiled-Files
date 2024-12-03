/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPVersion;

public class SOAP11Version
implements SOAPVersion,
SOAP11Constants {
    private static final SOAP11Version singleton = new SOAP11Version();

    public static SOAP11Version getSingleton() {
        return singleton;
    }

    private SOAP11Version() {
    }

    public String getEnvelopeURI() {
        return "http://schemas.xmlsoap.org/soap/envelope/";
    }

    public String getEncodingURI() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
    }

    public QName getRoleAttributeQName() {
        return QNAME_ACTOR;
    }

    public String getNextRoleURI() {
        return "http://schemas.xmlsoap.org/soap/actor/next";
    }

    public QName getMustUnderstandFaultCode() {
        return QNAME_MU_FAULTCODE;
    }

    public QName getSenderFaultCode() {
        return QNAME_SENDER_FAULTCODE;
    }

    public QName getReceiverFaultCode() {
        return QNAME_RECEIVER_FAULTCODE;
    }

    public QName getFaultReasonQName() {
        return QNAME_FAULT_REASON;
    }

    public QName getFaultCodeQName() {
        return QNAME_FAULT_CODE;
    }

    public QName getFaultDetailQName() {
        return QNAME_FAULT_DETAIL;
    }

    public QName getFaultRoleQName() {
        return QNAME_FAULT_ROLE;
    }
}

