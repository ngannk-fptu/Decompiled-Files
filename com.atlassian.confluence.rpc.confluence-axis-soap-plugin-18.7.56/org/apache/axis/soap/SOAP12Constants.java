/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.soap.SOAPConstants;

public class SOAP12Constants
implements SOAPConstants {
    private static QName headerQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Header");
    private static QName bodyQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Body");
    private static QName faultQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Fault");
    private static QName roleQName = new QName("http://www.w3.org/2003/05/soap-envelope", "role");
    public static final String PROP_WEBMETHOD = "soap12.webmethod";

    public String getEnvelopeURI() {
        return "http://www.w3.org/2003/05/soap-envelope";
    }

    public String getEncodingURI() {
        return "http://www.w3.org/2003/05/soap-encoding";
    }

    public QName getHeaderQName() {
        return headerQName;
    }

    public QName getBodyQName() {
        return bodyQName;
    }

    public QName getFaultQName() {
        return faultQName;
    }

    public QName getRoleAttributeQName() {
        return roleQName;
    }

    public String getContentType() {
        return "application/soap+xml";
    }

    public String getNextRoleURI() {
        return "http://www.w3.org/2003/05/soap-envelope/role/next";
    }

    public String getAttrHref() {
        return "ref";
    }

    public String getAttrItemType() {
        return "itemType";
    }

    public QName getVerMismatchFaultCodeQName() {
        return Constants.FAULT_SOAP12_VERSIONMISMATCH;
    }

    public QName getMustunderstandFaultQName() {
        return Constants.FAULT_SOAP12_MUSTUNDERSTAND;
    }

    public QName getArrayType() {
        return Constants.SOAP_ARRAY12;
    }
}

