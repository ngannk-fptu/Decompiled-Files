/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt;

import java.util.function.Supplier;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.xor.XOREncryptionInfoBuilder;

public enum EncryptionMode {
    binaryRC4(BinaryRC4EncryptionInfoBuilder::new, 1, 1, 0),
    cryptoAPI(CryptoAPIEncryptionInfoBuilder::new, 4, 2, 4),
    standard(StandardEncryptionInfoBuilder::new, 4, 2, 36),
    agile(AgileEncryptionInfoBuilder::new, 4, 4, 64),
    xor(XOREncryptionInfoBuilder::new, 0, 0, 0);

    public final Supplier<EncryptionInfoBuilder> builder;
    public final int versionMajor;
    public final int versionMinor;
    public final int encryptionFlags;

    private EncryptionMode(Supplier<EncryptionInfoBuilder> builder, int versionMajor, int versionMinor, int encryptionFlags) {
        this.builder = builder;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.encryptionFlags = encryptionFlags;
    }
}

