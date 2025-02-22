/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPath;
import org.bouncycastle.pkix.util.ErrorBundle;
import org.bouncycastle.pkix.util.LocalizedException;

public class CertPathReviewerException
extends LocalizedException {
    private int index = -1;
    private CertPath certPath = null;

    public CertPathReviewerException(ErrorBundle errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public CertPathReviewerException(ErrorBundle errorMessage) {
        super(errorMessage);
    }

    public CertPathReviewerException(ErrorBundle errorMessage, Throwable throwable, CertPath certPath, int index) {
        super(errorMessage, throwable);
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || index >= certPath.getCertificates().size()) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }

    public CertPathReviewerException(ErrorBundle errorMessage, CertPath certPath, int index) {
        super(errorMessage);
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || index >= certPath.getCertificates().size()) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }
}

