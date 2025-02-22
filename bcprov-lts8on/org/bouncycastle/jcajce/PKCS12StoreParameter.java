/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore;

public class PKCS12StoreParameter
implements KeyStore.LoadStoreParameter {
    private final OutputStream out;
    private final KeyStore.ProtectionParameter protectionParameter;
    private final boolean forDEREncoding;

    public PKCS12StoreParameter(OutputStream out, char[] password) {
        this(out, password, false);
    }

    public PKCS12StoreParameter(OutputStream out, KeyStore.ProtectionParameter protectionParameter) {
        this(out, protectionParameter, false);
    }

    public PKCS12StoreParameter(OutputStream out, char[] password, boolean forDEREncoding) {
        this(out, new KeyStore.PasswordProtection(password), forDEREncoding);
    }

    public PKCS12StoreParameter(OutputStream out, KeyStore.ProtectionParameter protectionParameter, boolean forDEREncoding) {
        this.out = out;
        this.protectionParameter = protectionParameter;
        this.forDEREncoding = forDEREncoding;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    @Override
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public boolean isForDEREncoding() {
        return this.forDEREncoding;
    }
}

