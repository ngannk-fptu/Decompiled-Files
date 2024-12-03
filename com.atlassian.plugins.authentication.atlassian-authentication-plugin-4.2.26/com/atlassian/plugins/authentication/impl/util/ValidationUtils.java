/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.impl.util;

import com.onelogin.saml2.util.Util;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.annotation.Nullable;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    @Nullable
    public static X509Certificate convertToCertificate(@Nullable String certificateString) {
        if (certificateString != null) {
            try {
                return Util.loadCert(certificateString);
            }
            catch (CertificateException e) {
                throw new IllegalArgumentException("Certificate is not parsable", e);
            }
        }
        return null;
    }

    @Nullable
    public static URL convertToUrl(@Nullable String urlString) {
        try {
            return urlString == null ? null : new URL(urlString);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided URL is malformed", e);
        }
    }
}

