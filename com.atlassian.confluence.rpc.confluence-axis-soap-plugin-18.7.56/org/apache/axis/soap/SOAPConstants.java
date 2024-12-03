/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAP12Constants;

public interface SOAPConstants
extends Serializable {
    public static final SOAP11Constants SOAP11_CONSTANTS = new SOAP11Constants();
    public static final SOAP12Constants SOAP12_CONSTANTS = new SOAP12Constants();

    public String getEnvelopeURI();

    public String getEncodingURI();

    public QName getFaultQName();

    public QName getHeaderQName();

    public QName getBodyQName();

    public QName getRoleAttributeQName();

    public String getContentType();

    public String getNextRoleURI();

    public String getAttrHref();

    public String getAttrItemType();

    public QName getVerMismatchFaultCodeQName();

    public QName getMustunderstandFaultQName();

    public QName getArrayType();
}

