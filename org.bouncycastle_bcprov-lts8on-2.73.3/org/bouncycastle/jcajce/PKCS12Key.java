/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.jcajce.PBKDFKey;

public class PKCS12Key
implements PBKDFKey {
    private final char[] password;
    private final boolean useWrongZeroLengthConversion;

    public PKCS12Key(char[] password) {
        this(password, false);
    }

    public PKCS12Key(char[] password, boolean useWrongZeroLengthConversion) {
        if (password == null) {
            password = new char[]{};
        }
        this.password = new char[password.length];
        this.useWrongZeroLengthConversion = useWrongZeroLengthConversion;
        System.arraycopy(password, 0, this.password, 0, password.length);
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public String getAlgorithm() {
        return "PKCS12";
    }

    @Override
    public String getFormat() {
        return "PKCS12";
    }

    @Override
    public byte[] getEncoded() {
        if (this.useWrongZeroLengthConversion && this.password.length == 0) {
            return new byte[2];
        }
        return PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
    }
}

