/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public class CertificateTooOldException
extends InvalidCertificateException {
    public CertificateTooOldException(ApplicationCertificate certificate, long certificateTimeout) {
        super(new TransportErrorMessage(TransportErrorMessage.Code.OLD_CERT, "Certificate too old. Application: {0} Certificate Created: {1} Timeout: {2}", certificate.getApplicationID(), String.valueOf(certificate.getCreationTime()), String.valueOf(certificateTimeout)));
    }
}

