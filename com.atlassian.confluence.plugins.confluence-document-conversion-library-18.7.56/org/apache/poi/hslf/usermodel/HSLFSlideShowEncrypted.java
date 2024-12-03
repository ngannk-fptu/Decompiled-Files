/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.EncryptedPowerPointFileException;
import org.apache.poi.hslf.record.DocumentEncryptionAtom;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.PositionDependentRecordAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.RecordFormatException;

@Internal
public class HSLFSlideShowEncrypted
implements Closeable {
    DocumentEncryptionAtom dea;
    EncryptionInfo _encryptionInfo;
    ChunkedCipherOutputStream cyos;
    private static final BitField fieldRecInst = new BitField(65520);
    private static final int[] BLIB_STORE_ENTRY_PARTS = new int[]{1, 1, 16, 2, 4, 4, 4, 1, 1, 1, 1};

    protected HSLFSlideShowEncrypted(DocumentEncryptionAtom dea) {
        this.dea = dea;
    }

    protected HSLFSlideShowEncrypted(byte[] docstream, NavigableMap<Integer, Record> recordMap) {
        UserEditAtom userEditAtomWithEncryption = null;
        for (Map.Entry me : recordMap.descendingMap().entrySet()) {
            UserEditAtom uea;
            Record r = (Record)me.getValue();
            if (!(r instanceof UserEditAtom) || (uea = (UserEditAtom)r).getEncryptSessionPersistIdRef() == -1) continue;
            userEditAtomWithEncryption = uea;
            break;
        }
        if (userEditAtomWithEncryption == null) {
            this.dea = null;
            return;
        }
        Record r = (Record)recordMap.get(userEditAtomWithEncryption.getPersistPointersOffset());
        if (!(r instanceof PersistPtrHolder)) {
            throw new RecordFormatException("Encountered an unexpected record-type: " + r);
        }
        PersistPtrHolder ptr = (PersistPtrHolder)r;
        Integer encOffset = ptr.getSlideLocationsLookup().get(userEditAtomWithEncryption.getEncryptSessionPersistIdRef());
        if (encOffset == null) {
            this.dea = null;
            return;
        }
        r = (Record)recordMap.get(encOffset);
        if (r == null) {
            r = Record.buildRecordAtOffset(docstream, encOffset);
            recordMap.put(encOffset, r);
        }
        this.dea = (DocumentEncryptionAtom)r;
        String pass = Biff8EncryptionKey.getCurrentUserPassword();
        EncryptionInfo ei = this.getEncryptionInfo();
        try {
            if (!ei.getDecryptor().verifyPassword(pass != null ? pass : "VelvetSweatshop")) {
                throw new EncryptedPowerPointFileException("PowerPoint file is encrypted. The correct password needs to be set via Biff8EncryptionKey.setCurrentUserPassword()");
            }
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedPowerPointFileException(e);
        }
    }

    public DocumentEncryptionAtom getDocumentEncryptionAtom() {
        return this.dea;
    }

    protected EncryptionInfo getEncryptionInfo() {
        return this.dea != null ? this.dea.getEncryptionInfo() : null;
    }

    protected OutputStream encryptRecord(OutputStream plainStream, int persistId, Record record) {
        boolean isPlain = this.dea == null || record instanceof UserEditAtom || record instanceof PersistPtrHolder || record instanceof DocumentEncryptionAtom;
        try {
            if (isPlain) {
                if (this.cyos != null) {
                    this.cyos.flush();
                }
                return plainStream;
            }
            if (this.cyos == null) {
                Encryptor enc = this.getEncryptionInfo().getEncryptor();
                enc.setChunkSize(-1);
                this.cyos = enc.getDataStream(plainStream, 0);
            }
            this.cyos.initCipherForBlock(persistId, false);
        }
        catch (Exception e) {
            throw new EncryptedPowerPointFileException(e);
        }
        return this.cyos;
    }

    private static void readFully(ChunkedCipherInputStream ccis, byte[] docstream, int offset, int len) throws IOException {
        if (IOUtils.readFully(ccis, docstream, offset, len) == -1) {
            throw new EncryptedPowerPointFileException("unexpected EOF");
        }
    }

    protected void decryptRecord(byte[] docstream, int persistId, int offset) {
        if (this.dea == null) {
            return;
        }
        Decryptor dec = this.getEncryptionInfo().getDecryptor();
        dec.setChunkSize(-1);
        try (LittleEndianByteArrayInputStream lei = new LittleEndianByteArrayInputStream(docstream, offset);
             ChunkedCipherInputStream ccis = (ChunkedCipherInputStream)dec.getDataStream(lei, docstream.length - offset, 0);){
            ccis.initCipherForBlock(persistId);
            HSLFSlideShowEncrypted.readFully(ccis, docstream, offset, 8);
            int rlen = (int)LittleEndian.getUInt(docstream, offset + 4);
            HSLFSlideShowEncrypted.readFully(ccis, docstream, offset + 8, rlen);
        }
        catch (Exception e) {
            throw new EncryptedPowerPointFileException(e);
        }
    }

    private void decryptPicBytes(byte[] pictstream, int offset, int len) throws IOException, GeneralSecurityException {
        LittleEndianByteArrayInputStream lei = new LittleEndianByteArrayInputStream(pictstream, offset);
        Decryptor dec = this.getEncryptionInfo().getDecryptor();
        ChunkedCipherInputStream ccis = (ChunkedCipherInputStream)dec.getDataStream(lei, len, 0);
        HSLFSlideShowEncrypted.readFully(ccis, pictstream, offset, len);
        ccis.close();
        lei.close();
    }

    protected void decryptPicture(byte[] pictstream, int offset) {
        if (this.dea == null) {
            return;
        }
        try {
            this.decryptPicBytes(pictstream, offset, 8);
            int recInst = fieldRecInst.getValue(LittleEndian.getUShort(pictstream, offset));
            int recType = LittleEndian.getUShort(pictstream, offset + 2);
            int rlen = (int)LittleEndian.getUInt(pictstream, offset + 4);
            int endOffset = (offset += 8) + rlen;
            if (recType == 61447) {
                for (int part : BLIB_STORE_ENTRY_PARTS) {
                    this.decryptPicBytes(pictstream, offset, part);
                }
                int cbName = LittleEndian.getUShort(pictstream, (offset += 36) - 3);
                if (cbName > 0) {
                    this.decryptPicBytes(pictstream, offset, cbName);
                    offset += cbName;
                }
                if (offset == endOffset) {
                    return;
                }
                this.decryptPicBytes(pictstream, offset, 8);
                recInst = fieldRecInst.getValue(LittleEndian.getUShort(pictstream, offset));
                recType = LittleEndian.getUShort(pictstream, offset + 2);
                offset += 8;
            }
            int rgbUidCnt = recInst == 535 || recInst == 981 || recInst == 1131 || recInst == 1347 || recInst == 1761 || recInst == 1763 || recInst == 1765 || recInst == 1961 ? 2 : 1;
            for (int i = 0; i < rgbUidCnt; ++i) {
                this.decryptPicBytes(pictstream, offset, 16);
                offset += 16;
            }
            int nextBytes = recType == 61466 || recType == 61467 || recType == 61468 ? 34 : 1;
            this.decryptPicBytes(pictstream, offset, nextBytes);
            int blipLen = endOffset - (offset += nextBytes);
            this.decryptPicBytes(pictstream, offset, blipLen);
        }
        catch (Exception e) {
            throw new CorruptPowerPointFileException(e);
        }
    }

    /*
     * Exception decompiling
     */
    protected void encryptPicture(byte[] pictstream, int offset) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected Record[] updateEncryptionRecord(Record[] records) {
        String password = Biff8EncryptionKey.getCurrentUserPassword();
        if (password == null) {
            if (this.dea == null) {
                return records;
            }
            this.dea = null;
            return HSLFSlideShowEncrypted.removeEncryptionRecord(records);
        }
        if (this.dea == null) {
            this.dea = new DocumentEncryptionAtom();
        }
        EncryptionInfo ei = this.dea.getEncryptionInfo();
        byte[] salt = ei.getVerifier().getSalt();
        Encryptor enc = this.getEncryptionInfo().getEncryptor();
        if (salt == null) {
            enc.confirmPassword(password);
        } else {
            byte[] verifier = ei.getDecryptor().getVerifier();
            enc.confirmPassword(password, null, null, verifier, salt, null);
        }
        records = HSLFSlideShowEncrypted.normalizeRecords(records);
        return HSLFSlideShowEncrypted.addEncryptionRecord(records, this.dea);
    }

    protected static Record[] normalizeRecords(Record[] records) {
        UserEditAtom uea = null;
        PositionDependentRecordAtom pph = null;
        TreeMap<Integer, Integer> slideLocations = new TreeMap<Integer, Integer>();
        TreeMap<Integer, Record> recordMap = new TreeMap<Integer, Record>();
        ArrayList<Integer> obsoleteOffsets = new ArrayList<Integer>();
        int duplicatedCount = 0;
        for (Record r : records) {
            assert (r instanceof PositionDependentRecord);
            PositionDependentRecord pdr = (PositionDependentRecord)((Object)r);
            if (pdr instanceof UserEditAtom) {
                uea = (UserEditAtom)pdr;
                continue;
            }
            if (pdr instanceof PersistPtrHolder) {
                if (pph != null) {
                    ++duplicatedCount;
                }
                pph = (PersistPtrHolder)pdr;
                for (Map.Entry<Integer, Integer> me : ((PersistPtrHolder)pph).getSlideLocationsLookup().entrySet()) {
                    Integer oldOffset = slideLocations.put(me.getKey(), me.getValue());
                    if (oldOffset == null) continue;
                    obsoleteOffsets.add(oldOffset);
                }
                continue;
            }
            recordMap.put(pdr.getLastOnDiskOffset(), r);
        }
        if (uea == null || pph == null || uea.getPersistPointersOffset() != pph.getLastOnDiskOffset()) {
            throw new EncryptedDocumentException("UserEditAtom and PersistPtrHolder must exist and their offset need to match.");
        }
        recordMap.put(pph.getLastOnDiskOffset(), pph);
        recordMap.put(uea.getLastOnDiskOffset(), uea);
        if (duplicatedCount == 0 && obsoleteOffsets.isEmpty()) {
            return records;
        }
        uea.setLastUserEditAtomOffset(0);
        ((PersistPtrHolder)pph).clear();
        for (Map.Entry entry : slideLocations.entrySet()) {
            ((PersistPtrHolder)pph).addSlideLookup((Integer)entry.getKey(), (Integer)entry.getValue());
        }
        for (Integer n : obsoleteOffsets) {
            recordMap.remove(n);
        }
        return recordMap.values().toArray(new Record[0]);
    }

    protected static Record[] removeEncryptionRecord(Record[] records) {
        int deaSlideId = -1;
        int deaOffset = -1;
        PersistPtrHolder ptr = null;
        UserEditAtom uea = null;
        ArrayList<Record> recordList = new ArrayList<Record>();
        for (Record r : records) {
            if (r instanceof DocumentEncryptionAtom) {
                deaOffset = ((DocumentEncryptionAtom)r).getLastOnDiskOffset();
                continue;
            }
            if (r instanceof UserEditAtom) {
                uea = (UserEditAtom)r;
                deaSlideId = uea.getEncryptSessionPersistIdRef();
                uea.setEncryptSessionPersistIdRef(-1);
            } else if (r instanceof PersistPtrHolder) {
                ptr = (PersistPtrHolder)r;
            }
            recordList.add(r);
        }
        if (ptr == null || uea == null) {
            throw new EncryptedDocumentException("UserEditAtom or PersistPtrholder not found.");
        }
        if (deaSlideId == -1 && deaOffset == -1) {
            return records;
        }
        TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>(ptr.getSlideLocationsLookup());
        ptr.clear();
        int maxSlideId = -1;
        for (Map.Entry<Integer, Integer> me : tm.entrySet()) {
            if (me.getKey() == deaSlideId || me.getValue() == deaOffset) continue;
            ptr.addSlideLookup(me.getKey(), me.getValue());
            maxSlideId = Math.max(me.getKey(), maxSlideId);
        }
        uea.setMaxPersistWritten(maxSlideId);
        records = recordList.toArray(new Record[0]);
        return records;
    }

    protected static Record[] addEncryptionRecord(Record[] records, DocumentEncryptionAtom dea) {
        assert (dea != null);
        int ueaIdx = -1;
        int ptrIdx = -1;
        int deaIdx = -1;
        int idx = -1;
        for (Record r : records) {
            ++idx;
            if (r instanceof UserEditAtom) {
                ueaIdx = idx;
                continue;
            }
            if (r instanceof PersistPtrHolder) {
                ptrIdx = idx;
                continue;
            }
            if (!(r instanceof DocumentEncryptionAtom)) continue;
            deaIdx = idx;
        }
        assert (ueaIdx != -1 && ptrIdx != -1 && ptrIdx < ueaIdx);
        if (deaIdx != -1) {
            DocumentEncryptionAtom deaOld = (DocumentEncryptionAtom)records[deaIdx];
            dea.setLastOnDiskOffset(deaOld.getLastOnDiskOffset());
            records[deaIdx] = dea;
            return records;
        }
        PersistPtrHolder ptr = (PersistPtrHolder)records[ptrIdx];
        UserEditAtom uea = (UserEditAtom)records[ueaIdx];
        dea.setLastOnDiskOffset(ptr.getLastOnDiskOffset() - 1);
        int nextSlideId = uea.getMaxPersistWritten() + 1;
        ptr.addSlideLookup(nextSlideId, ptr.getLastOnDiskOffset() - 1);
        uea.setEncryptSessionPersistIdRef(nextSlideId);
        uea.setMaxPersistWritten(nextSlideId);
        Record[] newRecords = new Record[records.length + 1];
        if (ptrIdx > 0) {
            System.arraycopy(records, 0, newRecords, 0, ptrIdx);
        }
        if (ptrIdx < records.length - 1) {
            System.arraycopy(records, ptrIdx, newRecords, ptrIdx + 1, records.length - ptrIdx);
        }
        newRecords[ptrIdx] = dea;
        return newRecords;
    }

    @Override
    public void close() throws IOException {
        if (this.cyos != null) {
            this.cyos.close();
        }
    }
}

