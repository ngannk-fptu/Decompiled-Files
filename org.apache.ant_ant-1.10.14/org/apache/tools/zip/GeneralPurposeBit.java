/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import org.apache.tools.zip.ZipShort;

public final class GeneralPurposeBit
implements Cloneable {
    private static final int ENCRYPTION_FLAG = 1;
    private static final int DATA_DESCRIPTOR_FLAG = 8;
    private static final int STRONG_ENCRYPTION_FLAG = 64;
    public static final int UFT8_NAMES_FLAG = 2048;
    private boolean languageEncodingFlag = false;
    private boolean dataDescriptorFlag = false;
    private boolean encryptionFlag = false;
    private boolean strongEncryptionFlag = false;

    public boolean usesUTF8ForNames() {
        return this.languageEncodingFlag;
    }

    public void useUTF8ForNames(boolean b) {
        this.languageEncodingFlag = b;
    }

    public boolean usesDataDescriptor() {
        return this.dataDescriptorFlag;
    }

    public void useDataDescriptor(boolean b) {
        this.dataDescriptorFlag = b;
    }

    public boolean usesEncryption() {
        return this.encryptionFlag;
    }

    public void useEncryption(boolean b) {
        this.encryptionFlag = b;
    }

    public boolean usesStrongEncryption() {
        return this.encryptionFlag && this.strongEncryptionFlag;
    }

    public void useStrongEncryption(boolean b) {
        this.strongEncryptionFlag = b;
        if (b) {
            this.useEncryption(true);
        }
    }

    public byte[] encode() {
        byte[] result = new byte[2];
        this.encode(result, 0);
        return result;
    }

    public void encode(byte[] buf, int offset) {
        ZipShort.putShort((this.dataDescriptorFlag ? 8 : 0) | (this.languageEncodingFlag ? 2048 : 0) | (this.encryptionFlag ? 1 : 0) | (this.strongEncryptionFlag ? 64 : 0), buf, offset);
    }

    public static GeneralPurposeBit parse(byte[] data, int offset) {
        int generalPurposeFlag = ZipShort.getValue(data, offset);
        GeneralPurposeBit b = new GeneralPurposeBit();
        b.useDataDescriptor((generalPurposeFlag & 8) != 0);
        b.useUTF8ForNames((generalPurposeFlag & 0x800) != 0);
        b.useStrongEncryption((generalPurposeFlag & 0x40) != 0);
        b.useEncryption((generalPurposeFlag & 1) != 0);
        return b;
    }

    public int hashCode() {
        return 3 * (7 * (13 * (17 * (this.encryptionFlag ? 1 : 0) + (this.strongEncryptionFlag ? 1 : 0)) + (this.languageEncodingFlag ? 1 : 0)) + (this.dataDescriptorFlag ? 1 : 0));
    }

    public boolean equals(Object o) {
        if (o instanceof GeneralPurposeBit) {
            GeneralPurposeBit g = (GeneralPurposeBit)o;
            return g.encryptionFlag == this.encryptionFlag && g.strongEncryptionFlag == this.strongEncryptionFlag && g.languageEncodingFlag == this.languageEncodingFlag && g.dataDescriptorFlag == this.dataDescriptorFlag;
        }
        return false;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("GeneralPurposeBit is not Cloneable?", ex);
        }
    }
}

