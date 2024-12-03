/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.X509CertificateHolder;

class ValidationUtils {
    ValidationUtils() {
    }

    static boolean isSelfIssued(X509CertificateHolder cert) {
        return cert.getSubject().equals((Object)cert.getIssuer());
    }
}

