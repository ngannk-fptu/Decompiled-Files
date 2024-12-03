/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.util.Arrays;

public class PKCS12KeyWithParameters
extends PKCS12Key
implements PBEKey {
    private final byte[] salt;
    private final int iterationCount;

    public PKCS12KeyWithParameters(char[] password, byte[] salt, int iterationCount) {
        super(password);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    public PKCS12KeyWithParameters(char[] password, boolean useWrongZeroLengthConversion, byte[] salt, int iterationCount) {
        super(password, useWrongZeroLengthConversion);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    @Override
    public byte[] getSalt() {
        return this.salt;
    }

    @Override
    public int getIterationCount() {
        return this.iterationCount;
    }
}

