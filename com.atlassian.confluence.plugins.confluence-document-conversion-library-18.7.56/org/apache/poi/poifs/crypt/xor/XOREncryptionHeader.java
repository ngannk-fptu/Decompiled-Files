/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.xor;

import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

public class XOREncryptionHeader
extends EncryptionHeader
implements EncryptionRecord {
    protected XOREncryptionHeader() {
    }

    protected XOREncryptionHeader(XOREncryptionHeader other) {
        super(other);
    }

    @Override
    public void write(LittleEndianByteArrayOutputStream leos) {
    }

    @Override
    public XOREncryptionHeader copy() {
        return new XOREncryptionHeader(this);
    }
}

