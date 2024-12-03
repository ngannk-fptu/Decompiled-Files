/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIDocument;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hwpf.model.CHPBinTable;
import org.apache.poi.hwpf.model.FibBase;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.FontTable;
import org.apache.poi.hwpf.model.ListTables;
import org.apache.poi.hwpf.model.PAPBinTable;
import org.apache.poi.hwpf.model.SectionTable;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.usermodel.ObjectPoolImpl;
import org.apache.poi.hwpf.usermodel.ObjectsPool;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

public abstract class HWPFDocumentCore
extends POIDocument {
    protected static final String STREAM_OBJECT_POOL = "ObjectPool";
    protected static final String STREAM_WORD_DOCUMENT = "WordDocument";
    protected static final String STREAM_TABLE_0 = "0Table";
    protected static final String STREAM_TABLE_1 = "1Table";
    private static final int DEFAULT_MAX_RECORD_LENGTH = 500000000;
    private static int MAX_RECORD_LENGTH = 500000000;
    protected static final int FIB_BASE_LEN = 68;
    protected static final int RC4_REKEYING_INTERVAL = 512;
    protected ObjectPoolImpl _objectPool;
    protected FileInformationBlock _fib;
    protected StyleSheet _ss;
    protected CHPBinTable _cbt;
    protected PAPBinTable _pbt;
    protected SectionTable _st;
    protected FontTable _ft;
    protected ListTables _lt;
    protected byte[] _mainStream;
    private EncryptionInfo _encryptionInfo;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected HWPFDocumentCore() {
        super((DirectoryNode)null);
    }

    public static POIFSFileSystem verifyAndBuildPOIFS(InputStream istream) throws IOException {
        InputStream is = FileMagic.prepareToCheckMagic(istream);
        FileMagic fm = FileMagic.valueOf(is);
        if (fm != FileMagic.OLE2) {
            throw new IllegalArgumentException("The document is really a " + (Object)((Object)fm) + " file");
        }
        return new POIFSFileSystem(is);
    }

    public HWPFDocumentCore(InputStream istream) throws IOException {
        this(HWPFDocumentCore.verifyAndBuildPOIFS(istream));
    }

    public HWPFDocumentCore(POIFSFileSystem pfilesystem) throws IOException {
        this(pfilesystem.getRoot());
    }

    public HWPFDocumentCore(DirectoryNode directory) throws IOException {
        super(directory);
        this._mainStream = this.getDocumentEntryBytes(STREAM_WORD_DOCUMENT, 68, Integer.MAX_VALUE);
        this._fib = new FileInformationBlock(this._mainStream);
        DirectoryEntry objectPoolEntry = null;
        if (directory.hasEntry(STREAM_OBJECT_POOL)) {
            objectPoolEntry = (DirectoryEntry)directory.getEntry(STREAM_OBJECT_POOL);
        }
        this._objectPool = new ObjectPoolImpl(objectPoolEntry);
    }

    public abstract Range getRange();

    public abstract Range getOverallRange();

    public String getDocumentText() {
        return this.getText().toString();
    }

    @Internal
    public abstract StringBuilder getText();

    public CHPBinTable getCharacterTable() {
        return this._cbt;
    }

    public PAPBinTable getParagraphTable() {
        return this._pbt;
    }

    public SectionTable getSectionTable() {
        return this._st;
    }

    public StyleSheet getStyleSheet() {
        return this._ss;
    }

    public ListTables getListTables() {
        return this._lt;
    }

    public FontTable getFontTable() {
        return this._ft;
    }

    public FileInformationBlock getFileInformationBlock() {
        return this._fib;
    }

    public ObjectsPool getObjectsPool() {
        return this._objectPool;
    }

    public abstract TextPieceTable getTextTable();

    @Internal
    public byte[] getMainStream() {
        return this._mainStream;
    }

    @Override
    public EncryptionInfo getEncryptionInfo() throws IOException {
        FibBase fibBase;
        if (this._encryptionInfo != null) {
            return this._encryptionInfo;
        }
        if (this._fib != null && this._fib.getFibBase() != null) {
            fibBase = this._fib.getFibBase();
        } else {
            byte[] fibBaseBytes = this._mainStream != null ? this._mainStream : this.getDocumentEntryBytes(STREAM_WORD_DOCUMENT, -1, 68);
            fibBase = new FibBase(fibBaseBytes, 0);
        }
        if (!fibBase.isFEncrypted()) {
            return null;
        }
        String tableStrmName = fibBase.isFWhichTblStm() ? STREAM_TABLE_1 : STREAM_TABLE_0;
        byte[] tableStream = this.getDocumentEntryBytes(tableStrmName, -1, fibBase.getLKey());
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(tableStream);
        EncryptionMode em = fibBase.isFObfuscated() ? EncryptionMode.xor : null;
        EncryptionInfo ei = new EncryptionInfo(leis, em);
        Decryptor dec = ei.getDecryptor();
        dec.setChunkSize(512);
        try {
            String pass = Biff8EncryptionKey.getCurrentUserPassword();
            if (pass == null) {
                pass = "VelvetSweatshop";
            }
            if (!dec.verifyPassword(pass)) {
                throw new EncryptedDocumentException("document is encrypted, password is invalid - use Biff8EncryptionKey.setCurrentUserPasswort() to set password before opening");
            }
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e.getMessage(), e);
        }
        this._encryptionInfo = ei;
        return ei;
    }

    protected void updateEncryptionInfo() {
        this.readProperties();
        String password = Biff8EncryptionKey.getCurrentUserPassword();
        FibBase fBase = this._fib.getFibBase();
        if (password == null) {
            fBase.setLKey(0);
            fBase.setFEncrypted(false);
            fBase.setFObfuscated(false);
            this._encryptionInfo = null;
        } else {
            if (this._encryptionInfo == null) {
                this._encryptionInfo = new EncryptionInfo(EncryptionMode.cryptoAPI);
                fBase.setFEncrypted(true);
                fBase.setFObfuscated(false);
            }
            Encryptor enc = this._encryptionInfo.getEncryptor();
            byte[] salt = this._encryptionInfo.getVerifier().getSalt();
            if (salt == null) {
                enc.confirmPassword(password);
            } else {
                byte[] verifier = this._encryptionInfo.getDecryptor().getVerifier();
                enc.confirmPassword(password, null, null, verifier, salt, null);
            }
        }
    }

    /*
     * Exception decompiling
     */
    protected byte[] getDocumentEntryBytes(String name, int encryptionOffset, int len) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
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

    private InputStream getDecryptedStream(DocumentInputStream dis, int streamSize, int encryptionOffset) throws IOException, GeneralSecurityException {
        Decryptor dec = this.getEncryptionInfo().getDecryptor();
        ChunkedCipherInputStream cis = (ChunkedCipherInputStream)dec.getDataStream(dis, streamSize, 0);
        byte[] plain = new byte[]{};
        if (encryptionOffset > 0) {
            plain = IOUtils.safelyAllocate(encryptionOffset, MAX_RECORD_LENGTH);
            cis.readPlain(plain, 0, encryptionOffset);
        }
        return new SequenceInputStream(new ByteArrayInputStream(plain), cis);
    }
}

