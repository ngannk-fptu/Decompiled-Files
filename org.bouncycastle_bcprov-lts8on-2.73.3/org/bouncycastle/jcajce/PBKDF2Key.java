/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDFKey;
import org.bouncycastle.util.Arrays;

public class PBKDF2Key
implements PBKDFKey {
    private final char[] password;
    private final CharToByteConverter converter;

    public PBKDF2Key(char[] password, CharToByteConverter converter) {
        this.password = Arrays.clone(password);
        this.converter = converter;
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public String getAlgorithm() {
        return "PBKDF2";
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

