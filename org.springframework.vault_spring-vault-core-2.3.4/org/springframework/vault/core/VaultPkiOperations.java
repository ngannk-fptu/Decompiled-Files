/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.core;

import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultCertificateResponse;
import org.springframework.vault.support.VaultSignCertificateRequestResponse;

public interface VaultPkiOperations {
    public VaultCertificateResponse issueCertificate(String var1, VaultCertificateRequest var2) throws VaultException;

    public VaultSignCertificateRequestResponse signCertificateRequest(String var1, String var2, VaultCertificateRequest var3) throws VaultException;

    public void revoke(String var1) throws VaultException;

    @Nullable
    public InputStream getCrl(Encoding var1) throws VaultException;

    public static enum Encoding {
        DER,
        PEM;

    }
}

