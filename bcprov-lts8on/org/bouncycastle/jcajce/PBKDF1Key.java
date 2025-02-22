/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDFKey;

public class PBKDF1Key
implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF1Key(char[] password, CharToByteConverter converter) {
        this.password = new char[password.length];
        this.converter = converter;
        System.arraycopy(password, 0, this.password, 0, password.length);
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public String getAlgorithm() {
        return "PBKDF1";
    }

    @Override
    public String getFormat() {
        return this.converter.getType();
    }

    @Override
    public byte[] getEncoded() {
        return this.converter.convert(this.password);
    }
}

