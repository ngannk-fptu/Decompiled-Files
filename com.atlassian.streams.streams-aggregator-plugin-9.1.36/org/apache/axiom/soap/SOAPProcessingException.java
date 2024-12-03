/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMException;

public class SOAPProcessingException
extends OMException {
    private static final long serialVersionUID = -5432612295193716211L;
    private String soapFaultCode;

    public SOAPProcessingException(String message) {
        super(message);
    }

    public SOAPProcessingException(Throwable cause) {
        super(cause);
    }

    public SOAPProcessingException(String messageText, String faultCode, Throwable cause) {
        super(messageText, cause);
        this.soapFaultCode = faultCode;
    }

    public SOAPProcessingException(String messageText, String faultCode) {
        super(messageText);
        this.soapFaultCode = faultCode;
    }

    public String getFaultCode() {
        return this.soapFaultCode;
    }

    public void setFaultCode(String soapFaultCode) {
        this.soapFaultCode = soapFaultCode;
    }
}

