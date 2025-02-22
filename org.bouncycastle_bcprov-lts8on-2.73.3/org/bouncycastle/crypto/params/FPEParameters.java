/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;

public final class FPEParameters
implements CipherParameters {
    private final KeyParameter key;
    private final RadixConverter radixConverter;
    private final byte[] tweak;
    private final boolean useInverse;

    public FPEParameters(KeyParameter key, int radix, byte[] tweak) {
        this(key, radix, tweak, false);
    }

    public FPEParameters(KeyParameter key, int radix, byte[] tweak, boolean useInverse) {
        this(key, new RadixConverter(radix), tweak, useInverse);
    }

    public FPEParameters(KeyParameter key, RadixConverter radixConverter, byte[] tweak, boolean useInverse) {
        this.key = key;
        this.radixConverter = radixConverter;
        this.tweak = Arrays.clone(tweak);
        this.useInverse = useInverse;
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public int getRadix() {
        return this.radixConverter.getRadix();
    }

    public RadixConverter getRadixConverter() {
        return this.radixConverter;
    }

    public byte[] getTweak() {
        return Arrays.clone(this.tweak);
    }

    public boolean isUsingInverseFunction() {
        return this.useInverse;
    }
}

