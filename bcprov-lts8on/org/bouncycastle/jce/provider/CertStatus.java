/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.util.Date;

class CertStatus {
    public static final int UNREVOKED = 11;
    public static final int UNDETERMINED = 12;
    int certStatus = 11;
    Date revocationDate = null;

    CertStatus() {
    }

    public Date getRevocationDate() {
        return this.revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public int getCertStatus() {
        return this.certStatus;
    }

    public void setCertStatus(int certStatus) {
        this.certStatus = certStatus;
    }
}

