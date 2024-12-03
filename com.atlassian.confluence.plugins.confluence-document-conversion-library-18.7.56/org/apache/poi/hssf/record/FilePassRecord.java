/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hssf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionHeader;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionVerifier;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionHeader;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionVerifier;
import org.apache.poi.poifs.crypt.xor.XOREncryptionHeader;
import org.apache.poi.poifs.crypt.xor.XOREncryptionVerifier;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

public final class FilePassRecord
extends StandardRecord {
    public static final short sid = 47;
    private static final int ENCRYPTION_XOR = 0;
    private static final int ENCRYPTION_OTHER = 1;
    private final int encryptionType;
    private final EncryptionInfo encryptionInfo;

    private FilePassRecord(FilePassRecord other) {
        super(other);
        this.encryptionType = other.encryptionType;
        this.encryptionInfo = other.encryptionInfo.copy();
    }

    public FilePassRecord(EncryptionMode encryptionMode) {
        this.encryptionType = encryptionMode == EncryptionMode.xor ? 0 : 1;
        this.encryptionInfo = new EncryptionInfo(encryptionMode);
    }

    public FilePassRecord(RecordInputStream in) {
        EncryptionMode preferredMode;
        this.encryptionType = in.readUShort();
        switch (this.encryptionType) {
            case 0: {
                preferredMode = EncryptionMode.xor;
                break;
            }
            case 1: {
                preferredMode = EncryptionMode.cryptoAPI;
                break;
            }
            default: {
                throw new EncryptedDocumentException("invalid encryption type");
            }
        }
        try {
            this.encryptionInfo = new EncryptionInfo(in, preferredMode);
        }
        catch (IOException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.encryptionType);
        byte[] data = new byte[1024];
        try (LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(data, 0);){
            switch (this.encryptionInfo.getEncryptionMode()) {
                case xor: {
                    ((XOREncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((XOREncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                case binaryRC4: {
                    out.writeShort(this.encryptionInfo.getVersionMajor());
                    out.writeShort(this.encryptionInfo.getVersionMinor());
                    ((BinaryRC4EncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((BinaryRC4EncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                case cryptoAPI: {
                    out.writeShort(this.encryptionInfo.getVersionMajor());
                    out.writeShort(this.encryptionInfo.getVersionMinor());
                    out.writeInt(this.encryptionInfo.getEncryptionFlags());
                    ((CryptoAPIEncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((CryptoAPIEncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                default: {
                    throw new EncryptedDocumentException("not supported");
                }
            }
            out.write(data, 0, bos.getWriteIndex());
        }
        catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override
    protected int getDataSize() {
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
        LittleEndianOutputStream leos = new LittleEndianOutputStream((OutputStream)bos);
        this.serialize(leos);
        return bos.size();
    }

    public EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }

    @Override
    public short getSid() {
        return 47;
    }

    @Override
    public FilePassRecord copy() {
        return new FilePassRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FILE_PASS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("type", () -> this.encryptionType, "encryptionInfo", this::getEncryptionInfo);
    }
}

