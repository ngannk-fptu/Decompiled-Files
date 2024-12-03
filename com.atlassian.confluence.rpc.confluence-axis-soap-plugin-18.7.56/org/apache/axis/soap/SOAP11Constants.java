/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.soap.SOAPConstants;

public class SOAP11Constants
implements SOAPConstants {
    private static QName headerQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
    private static QName bodyQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
    private static QName faultQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
    private static QName roleQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor");

    public String getEnvelopeURI() {
        return "http://schemas.xmlsoap.org/soap/envelope/";
    }

    public String getEncodingURI() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
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
        return "text/xml";
    }

    public String getNextRoleURI() {
        return "http://schemas.xmlsoap.org/soap/actor/next";
    }

    public String getAttrHref() {
        return "href";
    }

    public String getAttrItemType() {
        return "arrayType";
    }

    public QName getVerMismatchFaultCodeQName() {
        return Constants.FAULT_VERSIONMISMATCH;
    }

    public QName getMustunderstandFaultQName() {
        return Constants.FAULT_MUSTUNDERSTAND;
    }

    public QName getArrayType() {
        return Constants.SOAP_ARRAY;
    }
}

