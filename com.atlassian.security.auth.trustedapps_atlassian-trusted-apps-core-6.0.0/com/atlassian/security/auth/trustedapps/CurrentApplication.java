/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

@Deprecated
public interface CurrentApplication
extends Application {
    @Deprecated
    public static final String HEADER_TRUSTED_APP_ID = "X-Seraph-Trusted-App-ID";
    @Deprecated
    public static final String HEADER_TRUSTED_APP_CERT = "X-Seraph-Trusted-App-Cert";
    @Deprecated
    public static final String HEADER_TRUSTED_APP_SECRET_KEY = "X-Seraph-Trusted-App-Key";
    @Deprecated
    public static final String HEADER_TRUSTED_APP_ERROR = "X-Seraph-Trusted-App-Error";
    @Deprecated
    public static final String HEADER_TRUSTED_APP_STATUS = "X-Seraph-Trusted-App-Status";

    @Deprecated
    public EncryptedCertificate encode(String var1);

    public EncryptedCertificate encode(String var1, String var2);
}

