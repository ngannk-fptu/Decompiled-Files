/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public class HKDFParameters
implements DerivationParameters {
    private final byte[] ikm;
    private final boolean skipExpand;
    private final byte[] salt;
    private final byte[] info;

    private HKDFParameters(byte[] ikm, boolean skip, byte[] salt, byte[] info) {
        if (ikm == null) {
            throw new IllegalArgumentException("IKM (input keying material) should not be null");
        }
        this.ikm = Arrays.clone(ikm);
        this.skipExpand = skip;
        this.salt = (byte[])(salt == null || salt.length == 0 ? null : Arrays.clone(salt));
        this.info = info == null ? new byte[0] : Arrays.clone(info);
    }

    public HKDFParameters(byte[] ikm, byte[] salt, byte[] info) {
        this(ikm, false, salt, info);
    }

    public static HKDFParameters skipExtractParameters(byte[] ikm, byte[] info) {
        return new HKDFParameters(ikm, true, null, info);
    }

    public static HKDFParameters defaultParameters(byte[] ikm) {
        return new HKDFParameters(ikm, false, null, null);
    }

    public byte[] getIKM() {
        return Arrays.clone(this.ikm);
    }

    public boolean skipExtract() {
        return this.skipExpand;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public byte[] getInfo() {
        return Arrays.clone(this.info);
    }
}

