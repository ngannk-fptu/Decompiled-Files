/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import javax.servlet.http.HttpServletRequest;

@Deprecated
public interface TrustedApplication
extends Application {
    public ApplicationCertificate decode(EncryptedCertificate var1, HttpServletRequest var2) throws InvalidCertificateException;

    public boolean verifySignature(long var1, String var3, String var4, String var5) throws UnableToVerifySignatureException;

    public RequestConditions getRequestConditions();

    public String getName();
}

