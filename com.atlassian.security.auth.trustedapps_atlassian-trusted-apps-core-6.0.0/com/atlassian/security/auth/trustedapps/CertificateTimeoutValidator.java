/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.CertificateTooOldException;
import com.atlassian.security.auth.trustedapps.Clock;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import java.math.BigInteger;

public class CertificateTimeoutValidator {
    private final Clock clock;

    public CertificateTimeoutValidator(Clock clock) {
        this.clock = clock;
    }

    public ApplicationCertificate checkCertificateExpiry(ApplicationCertificate certificate, long certificateTimeout) throws InvalidCertificateException {
        if (certificateTimeout != 0L) {
            BigInteger created = BigInteger.valueOf(certificate.getCreationTime().getTime());
            BigInteger ttl = BigInteger.valueOf(certificateTimeout);
            BigInteger now = BigInteger.valueOf(this.clock.currentTimeMillis());
            if (created.add(ttl).compareTo(now) < 0) {
                throw new CertificateTooOldException(certificate, certificateTimeout);
            }
        }
        return certificate;
    }
}

