/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;

public class SignatureValueSecurityEvent
extends SecurityEvent {
    private byte[] signatureValue;

    public SignatureValueSecurityEvent() {
        super(SecurityEventConstants.SignatureValue);
    }

    public byte[] getSignatureValue() {
        return this.signatureValue;
    }

    public void setSignatureValue(byte[] signatureValue) {
        this.signatureValue = signatureValue;
    }
}

