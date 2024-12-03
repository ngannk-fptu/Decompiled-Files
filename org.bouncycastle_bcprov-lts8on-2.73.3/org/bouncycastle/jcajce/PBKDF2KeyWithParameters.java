/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.util.Arrays;

public class PBKDF2KeyWithParameters
extends PBKDF2Key
implements PBEKey {
    private final byte[] salt;
    private final int iterationCount;

    public PBKDF2KeyWithParameters(char[] password, CharToByteConverter converter, byte[] salt, int iterationCount) {
        super(password, converter);
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

