/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import javax.xml.namespace.QName;

public interface SOAPVersion {
    public String getEnvelopeURI();

    public String getEncodingURI();

    public QName getRoleAttributeQName();

    public String getNextRoleURI();

    public QName getMustUnderstandFaultCode();

    public QName getSenderFaultCode();

    public QName getReceiverFaultCode();

    public QName getFaultReasonQName();

    public QName getFaultCodeQName();

    public QName getFaultDetailQName();

    public QName getFaultRoleQName();
}

