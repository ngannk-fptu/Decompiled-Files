/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPVersion;

public class SOAP12Version
implements SOAPVersion,
SOAP12Constants {
    private static final SOAP12Version singleton = new SOAP12Version();

    public static SOAP12Version getSingleton() {
        return singleton;
    }

    private SOAP12Version() {
    }

    public String getEnvelopeURI() {
        return "http://www.w3.org/2003/05/soap-envelope";
    }

    public String getEncodingURI() {
        return "http://www.w3.org/2003/05/soap-encoding";
    }

    public QName getRoleAttributeQName() {
        return QNAME_ROLE;
    }

    public String getNextRoleURI() {
        return "http://www.w3.org/2003/05/soap-envelope/role/next";
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

