/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import java.security.cert.Certificate;

public class PdfPublicKeyRecipient {
    private Certificate certificate = null;
    private int permission = 0;
    protected byte[] cms = null;

    public PdfPublicKeyRecipient(Certificate certificate, int permission) {
        this.certificate = certificate;
        this.permission = permission;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public int getPermission() {
        return this.permission;
    }

    protected void setCms(byte[] cms) {
        this.cms = cms;
    }

    protected byte[] getCms() {
        return this.cms;
    }
}

