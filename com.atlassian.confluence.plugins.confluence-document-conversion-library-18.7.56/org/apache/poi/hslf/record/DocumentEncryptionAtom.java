/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hslf.record.PositionDependentRecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionHeader;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionVerifier;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInputStream;

public final class DocumentEncryptionAtom
extends PositionDependentRecordAtom {
    private static final long _type = RecordTypes.DocumentEncryptionAtom.typeID;
    private final byte[] _header;
    private EncryptionInfo ei;

    protected DocumentEncryptionAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        ByteArrayInputStream bis = new ByteArrayInputStream(source, start + 8, len - 8);
        try (LittleEndianInputStream leis = new LittleEndianInputStream(bis);){
            this.ei = new EncryptionInfo(leis, EncryptionMode.cryptoAPI);
        }
        catch (IOException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    public DocumentEncryptionAtom() {
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 0, (short)15);
        LittleEndian.putShort(this._header, 2, (short)_type);
        this.ei = new EncryptionInfo(EncryptionMode.cryptoAPI);
    }

    public void initializeEncryptionInfo(int keyBits) {
        this.ei = new EncryptionInfo(EncryptionMode.cryptoAPI, CipherAlgorithm.rc4, HashAlgorithm.sha1, keyBits, -1, null);
    }

    public int getKeyLength() {
        return this.ei.getHeader().getKeySize();
    }

    public String getEncryptionProviderName() {
        return this.ei.getHeader().getCspName();
    }

    public EncryptionInfo getEncryptionInfo() {
        return this.ei;
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        byte[] data = new byte[1024];
        LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(data, 0);
        bos.writeShort(this.ei.getVersionMajor());
        bos.writeShort(this.ei.getVersionMinor());
        bos.writeInt(this.ei.getEncryptionFlags());
        ((CryptoAPIEncryptionHeader)this.ei.getHeader()).write(bos);
        ((CryptoAPIEncryptionVerifier)this.ei.getVerifier()).write(bos);
        LittleEndian.putInt(this._header, 4, bos.getWriteIndex());
        out.write(this._header);
        out.write(data, 0, bos.getWriteIndex());
        bos.close();
    }

    @Override
    public void updateOtherRecordReferences(Map<Integer, Integer> oldToNewReferencesLookup) {
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("encryptionInfo", this::getEncryptionInfo);
    }
}

