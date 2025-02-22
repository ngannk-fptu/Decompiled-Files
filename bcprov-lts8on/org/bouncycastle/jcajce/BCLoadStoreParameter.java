/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

public class BCLoadStoreParameter
implements KeyStore.LoadStoreParameter {
    private final InputStream in;
    private final OutputStream out;
    private final KeyStore.ProtectionParameter protectionParameter;

    public BCLoadStoreParameter(OutputStream out, char[] password) {
        this(out, (KeyStore.ProtectionParameter)new KeyStore.PasswordProtection(password));
    }

    public BCLoadStoreParameter(InputStream in, char[] password) {
        this(in, (KeyStore.ProtectionParameter)new KeyStore.PasswordProtection(password));
    }

    public BCLoadStoreParameter(InputStream in, KeyStore.ProtectionParameter protectionParameter) {
        this(in, null, protectionParameter);
    }

    public BCLoadStoreParameter(OutputStream out, KeyStore.ProtectionParameter protectionParameter) {
        this(null, out, protectionParameter);
    }

    BCLoadStoreParameter(InputStream in, OutputStream out, KeyStore.ProtectionParameter protectionParameter) {
        this.in = in;
        this.out = out;
        this.protectionParameter = protectionParameter;
    }

    @Override
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public OutputStream getOutputStream() {
        if (this.out == null) {
            throw new UnsupportedOperationException("parameter not configured for storage - no OutputStream");
        }
        return this.out;
    }

    public InputStream getInputStream() {
        if (this.out != null) {
            throw new UnsupportedOperationException("parameter configured for storage OutputStream present");
        }
        return this.in;
    }
}

