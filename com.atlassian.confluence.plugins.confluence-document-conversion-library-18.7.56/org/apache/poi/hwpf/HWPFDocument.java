/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.OldWordFileFormatException;
import org.apache.poi.hwpf.model.BookmarksTables;
import org.apache.poi.hwpf.model.CHPBinTable;
import org.apache.poi.hwpf.model.ComplexFileTable;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.hwpf.model.FSPADocumentPart;
import org.apache.poi.hwpf.model.FSPATable;
import org.apache.poi.hwpf.model.FieldsTables;
import org.apache.poi.hwpf.model.FontTable;
import org.apache.poi.hwpf.model.ListTables;
import org.apache.poi.hwpf.model.NoteType;
import org.apache.poi.hwpf.model.NotesTables;
import org.apache.poi.hwpf.model.OfficeArtContent;
import org.apache.poi.hwpf.model.PAPBinTable;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.model.RevisionMarkAuthorTable;
import org.apache.poi.hwpf.model.SavedByTable;
import org.apache.poi.hwpf.model.SectionTable;
import org.apache.poi.hwpf.model.SinglentonTextPiece;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.SubdocumentType;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.BookmarksImpl;
import org.apache.poi.hwpf.usermodel.Fields;
import org.apache.poi.hwpf.usermodel.FieldsImpl;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.Notes;
import org.apache.poi.hwpf.usermodel.NotesImpl;
import org.apache.poi.hwpf.usermodel.OfficeDrawings;
import org.apache.poi.hwpf.usermodel.OfficeDrawingsImpl;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

public final class HWPFDocument
extends HWPFDocumentCore {
    static final String PROPERTY_PRESERVE_BIN_TABLES = "org.apache.poi.hwpf.preserveBinTables";
    private static final String PROPERTY_PRESERVE_TEXT_TABLE = "org.apache.poi.hwpf.preserveTextTable";
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    private static int MAX_RECORD_LENGTH = 100000;
    private static final String STREAM_DATA = "Data";
    private byte[] _tableStream;
    private byte[] _dataStream;
    private DocumentProperties _dop;
    private ComplexFileTable _cft;
    private StringBuilder _text;
    private SavedByTable _sbt;
    private RevisionMarkAuthorTable _rmat;
    private FSPATable _fspaHeaders;
    private FSPATable _fspaMain;
    private final OfficeArtContent officeArtContent;
    private PicturesTable _pictures;
    private OfficeDrawingsImpl _officeDrawingsHeaders;
    private OfficeDrawingsImpl _officeDrawingsMain;
    private BookmarksTables _bookmarksTables;
    private Bookmarks _bookmarks;
    private NotesTables _endnotesTables = new NotesTables(NoteType.ENDNOTE);
    private Notes _endnotes = new NotesImpl(this._endnotesTables);
    private NotesTables _footnotesTables = new NotesTables(NoteType.FOOTNOTE);
    private Notes _footnotes = new NotesImpl(this._footnotesTables);
    private FieldsTables _fieldsTables;
    private Fields _fields;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HWPFDocument(InputStream istream) throws IOException {
        this(HWPFDocument.verifyAndBuildPOIFS(istream));
    }

    public HWPFDocument(POIFSFileSystem pfilesystem) throws IOException {
        this(pfilesystem.getRoot());
    }

    public HWPFDocument(DirectoryNode directory) throws IOException {
        super(directory);
        String name;
        if (this._fib.getFibBase().getNFib() < 106) {
            throw new OldWordFileFormatException("The document is too old - Word 95 or older. Try HWPFOldDocument instead?");
        }
        String string = name = this._fib.getFibBase().isFWhichTblStm() ? "1Table" : "0Table";
        if (!directory.hasEntry(name)) {
            throw new IllegalStateException("Table Stream '" + name + "' wasn't found - Either the document is corrupt, or is Word95 (or earlier)");
        }
        this._tableStream = this.getDocumentEntryBytes(name, this._fib.getFibBase().getLKey(), Integer.MAX_VALUE);
        this._fib.fillVariableFields(this._mainStream, this._tableStream);
        this._dataStream = directory.hasEntry(STREAM_DATA) ? this.getDocumentEntryBytes(STREAM_DATA, 0, Integer.MAX_VALUE) : new byte[]{};
        int fcMin = 0;
        this._dop = new DocumentProperties(this._tableStream, this._fib.getFcDop(), this._fib.getLcbDop());
        this._cft = new ComplexFileTable(this._mainStream, this._tableStream, this._fib.getFcClx(), fcMin);
        TextPieceTable _tpt = this._cft.getTextPieceTable();
        this._cbt = new CHPBinTable(this._mainStream, this._tableStream, this._fib.getFcPlcfbteChpx(), this._fib.getLcbPlcfbteChpx(), _tpt);
        this._pbt = new PAPBinTable(this._mainStream, this._tableStream, this._dataStream, this._fib.getFcPlcfbtePapx(), this._fib.getLcbPlcfbtePapx(), _tpt);
        this._text = _tpt.getText();
        boolean preserveBinTables = false;
        try {
            preserveBinTables = Boolean.parseBoolean(System.getProperty(PROPERTY_PRESERVE_BIN_TABLES));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!preserveBinTables) {
            this._cbt.rebuild(this._cft);
            this._pbt.rebuild(this._text, this._cft);
        }
        boolean preserveTextTable = false;
        try {
            preserveTextTable = Boolean.parseBoolean(System.getProperty(PROPERTY_PRESERVE_TEXT_TABLE));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!preserveTextTable) {
            this._cft = new ComplexFileTable();
            _tpt = this._cft.getTextPieceTable();
            SinglentonTextPiece textPiece = new SinglentonTextPiece(this._text);
            _tpt.add(textPiece);
            this._text = textPiece.getStringBuilder();
        }
        this._fspaHeaders = new FSPATable(this._tableStream, this._fib, FSPADocumentPart.HEADER);
        this._fspaMain = new FSPATable(this._tableStream, this._fib, FSPADocumentPart.MAIN);
        this.officeArtContent = new OfficeArtContent(this._tableStream, this._fib.getFcDggInfo(), this._fib.getLcbDggInfo());
        this._pictures = new PicturesTable(this, this._dataStream, this._mainStream, this._fspaMain, this.officeArtContent);
        this._officeDrawingsHeaders = new OfficeDrawingsImpl(this._fspaHeaders, this.officeArtContent, this._mainStream);
        this._officeDrawingsMain = new OfficeDrawingsImpl(this._fspaMain, this.officeArtContent, this._mainStream);
        this._st = new SectionTable(this._mainStream, this._tableStream, this._fib.getFcPlcfsed(), this._fib.getLcbPlcfsed(), fcMin, _tpt, this._fib.getSubdocumentTextStreamLength(SubdocumentType.MAIN));
        this._ss = new StyleSheet(this._tableStream, this._fib.getFcStshf());
        this._ft = new FontTable(this._tableStream, this._fib.getFcSttbfffn(), this._fib.getLcbSttbfffn());
        int listOffset = this._fib.getFcPlfLst();
        if (listOffset != 0 && this._fib.getLcbPlfLst() != 0) {
            this._lt = new ListTables(this._tableStream, listOffset, this._fib.getFcPlfLfo(), this._fib.getLcbPlfLfo());
        }
        int sbtOffset = this._fib.getFcSttbSavedBy();
        int sbtLength = this._fib.getLcbSttbSavedBy();
        if (sbtOffset != 0 && sbtLength != 0) {
            this._sbt = new SavedByTable(this._tableStream, sbtOffset, sbtLength);
        }
        int rmarkOffset = this._fib.getFcSttbfRMark();
        int rmarkLength = this._fib.getLcbSttbfRMark();
        if (rmarkOffset != 0 && rmarkLength != 0) {
            this._rmat = new RevisionMarkAuthorTable(this._tableStream, rmarkOffset, rmarkLength);
        }
        this._bookmarksTables = new BookmarksTables(this._tableStream, this._fib);
        this._bookmarks = new BookmarksImpl(this._bookmarksTables);
        this._endnotesTables = new NotesTables(NoteType.ENDNOTE, this._tableStream, this._fib);
        this._endnotes = new NotesImpl(this._endnotesTables);
        this._footnotesTables = new NotesTables(NoteType.FOOTNOTE, this._tableStream, this._fib);
        this._footnotes = new NotesImpl(this._footnotesTables);
        this._fieldsTables = new FieldsTables(this._tableStream, this._fib);
        this._fields = new FieldsImpl(this._fieldsTables);
    }

    @Override
    @Internal
    public TextPieceTable getTextTable() {
        return this._cft.getTextPieceTable();
    }

    @Override
    @Internal
    public StringBuilder getText() {
        return this._text;
    }

    public DocumentProperties getDocProperties() {
        return this._dop;
    }

    @Override
    public Range getOverallRange() {
        return new Range(0, this._text.length(), this);
    }

    @Override
    public Range getRange() {
        return this.getRange(SubdocumentType.MAIN);
    }

    private Range getRange(SubdocumentType subdocument) {
        int startCp = 0;
        for (SubdocumentType previos : SubdocumentType.ORDERED) {
            int length = this.getFileInformationBlock().getSubdocumentTextStreamLength(previos);
            if (subdocument == previos) {
                return new Range(startCp, startCp + length, this);
            }
            startCp += length;
        }
        throw new UnsupportedOperationException("Subdocument type not supported: " + (Object)((Object)subdocument));
    }

    public Range getFootnoteRange() {
        return this.getRange(SubdocumentType.FOOTNOTE);
    }

    public Range getEndnoteRange() {
        return this.getRange(SubdocumentType.ENDNOTE);
    }

    public Range getCommentsRange() {
        return this.getRange(SubdocumentType.ANNOTATION);
    }

    public Range getMainTextboxRange() {
        return this.getRange(SubdocumentType.TEXTBOX);
    }

    public Range getHeaderStoryRange() {
        return this.getRange(SubdocumentType.HEADER);
    }

    public int characterLength() {
        return this._text.length();
    }

    @Internal
    public SavedByTable getSavedByTable() {
        return this._sbt;
    }

    @Internal
    public RevisionMarkAuthorTable getRevisionMarkAuthorTable() {
        return this._rmat;
    }

    public PicturesTable getPicturesTable() {
        return this._pictures;
    }

    @Internal
    public OfficeArtContent getOfficeArtContent() {
        return this.officeArtContent;
    }

    public OfficeDrawings getOfficeDrawingsHeaders() {
        return this._officeDrawingsHeaders;
    }

    public OfficeDrawings getOfficeDrawingsMain() {
        return this._officeDrawingsMain;
    }

    public Bookmarks getBookmarks() {
        return this._bookmarks;
    }

    public Notes getEndnotes() {
        return this._endnotes;
    }

    public Notes getFootnotes() {
        return this._footnotes;
    }

    public Fields getFields() {
        return this._fields;
    }

    @Override
    public void write() throws IOException {
        this.validateInPlaceWritePossible();
        this.write(this.getDirectory().getFileSystem(), false);
        this.getDirectory().getFileSystem().writeFilesystem();
    }

    @Override
    public void write(File newFile) throws IOException {
        POIFSFileSystem pfs = POIFSFileSystem.create(newFile);
        this.write(pfs, true);
        pfs.writeFilesystem();
    }

    @Override
    public void write(OutputStream out) throws IOException {
        POIFSFileSystem pfs = new POIFSFileSystem();
        this.write(pfs, true);
        pfs.writeFilesystem(out);
    }

    private void write(POIFSFileSystem pfs, boolean copyOtherEntries) throws IOException {
        this._fib.clearOffsetsSizes();
        int fibSize = this._fib.getSize();
        fibSize += 512 - fibSize % 512;
        HWPFFileSystem docSys = new HWPFFileSystem();
        ByteArrayOutputStream wordDocumentStream = docSys.getStream("WordDocument");
        ByteArrayOutputStream tableStream = docSys.getStream("1Table");
        byte[] placeHolder = IOUtils.safelyAllocate(fibSize, MAX_RECORD_LENGTH);
        wordDocumentStream.write(placeHolder);
        int mainOffset = wordDocumentStream.size();
        int tableOffset = 0;
        this.updateEncryptionInfo();
        EncryptionInfo ei = this.getEncryptionInfo();
        if (ei != null) {
            byte[] buf = new byte[1000];
            LittleEndianByteArrayOutputStream leos = new LittleEndianByteArrayOutputStream(buf, 0);
            leos.writeShort(ei.getVersionMajor());
            leos.writeShort(ei.getVersionMinor());
            if (ei.getEncryptionMode() == EncryptionMode.cryptoAPI) {
                leos.writeInt(ei.getEncryptionFlags());
            }
            ((EncryptionRecord)((Object)ei.getHeader())).write(leos);
            ((EncryptionRecord)((Object)ei.getVerifier())).write(leos);
            tableStream.write(buf, 0, leos.getWriteIndex());
            this._fib.getFibBase().setLKey(tableOffset += leos.getWriteIndex());
        }
        this._fib.setFcStshf(tableOffset);
        this._ss.writeTo(tableStream);
        this._fib.setLcbStshf(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        this._fib.setFcClx(tableOffset);
        this._cft.writeTo(wordDocumentStream, tableStream);
        this._fib.setLcbClx(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        int fcMac = wordDocumentStream.size();
        this._fib.setFcDop(tableOffset);
        this._dop.writeTo(tableStream);
        this._fib.setLcbDop(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        if (this._bookmarksTables != null) {
            this._bookmarksTables.writePlcfBkmkf(this._fib, tableStream);
            tableOffset = tableStream.size();
        }
        if (this._bookmarksTables != null) {
            this._bookmarksTables.writePlcfBkmkl(this._fib, tableStream);
            tableOffset = tableStream.size();
        }
        this._fib.setFcPlcfbteChpx(tableOffset);
        this._cbt.writeTo(wordDocumentStream, tableStream, mainOffset, this._cft.getTextPieceTable());
        this._fib.setLcbPlcfbteChpx(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        this._fib.setFcPlcfbtePapx(tableOffset);
        this._pbt.writeTo(wordDocumentStream, tableStream, this._cft.getTextPieceTable());
        this._fib.setLcbPlcfbtePapx(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        this._endnotesTables.writeRef(this._fib, tableStream);
        this._endnotesTables.writeTxt(this._fib, tableStream);
        tableOffset = tableStream.size();
        if (this._fieldsTables != null) {
            this._fieldsTables.write(this._fib, tableStream);
            tableOffset = tableStream.size();
        }
        this._footnotesTables.writeRef(this._fib, tableStream);
        this._footnotesTables.writeTxt(this._fib, tableStream);
        tableOffset = tableStream.size();
        this._fib.setFcPlcfsed(tableOffset);
        this._st.writeTo(wordDocumentStream, tableStream);
        this._fib.setLcbPlcfsed(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        if (this._lt != null) {
            this._lt.writeListDataTo(this._fib, tableStream);
            tableOffset = tableStream.size();
            this._lt.writeListOverridesTo(this._fib, tableStream);
            tableOffset = tableStream.size();
        }
        if (this._bookmarksTables != null) {
            this._bookmarksTables.writeSttbfBkmk(this._fib, tableStream);
            tableOffset = tableStream.size();
        }
        if (this._sbt != null) {
            this._fib.setFcSttbSavedBy(tableOffset);
            this._sbt.writeTo(tableStream);
            this._fib.setLcbSttbSavedBy(tableStream.size() - tableOffset);
            tableOffset = tableStream.size();
        }
        if (this._rmat != null) {
            this._fib.setFcSttbfRMark(tableOffset);
            this._rmat.writeTo(tableStream);
            this._fib.setLcbSttbfRMark(tableStream.size() - tableOffset);
            tableOffset = tableStream.size();
        }
        this._fib.setFcSttbfffn(tableOffset);
        this._ft.writeTo(tableStream);
        this._fib.setLcbSttbfffn(tableStream.size() - tableOffset);
        tableOffset = tableStream.size();
        this._fib.getFibBase().setFcMin(mainOffset);
        this._fib.getFibBase().setFcMac(fcMac);
        this._fib.setCbMac(wordDocumentStream.size());
        byte[] mainBuf = HWPFDocument.fillUp4096(wordDocumentStream);
        this._fib.getFibBase().setFWhichTblStm(true);
        this._fib.writeTo(mainBuf, tableStream);
        byte[] tableBuf = HWPFDocument.fillUp4096(tableStream);
        byte[] dataBuf = HWPFDocument.fillUp4096(this._dataStream);
        if (ei == null) {
            HWPFDocument.write(pfs, mainBuf, "WordDocument");
            HWPFDocument.write(pfs, tableBuf, "1Table");
            HWPFDocument.write(pfs, dataBuf, STREAM_DATA);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(100000);
            this.encryptBytes(mainBuf, 68, bos);
            HWPFDocument.write(pfs, bos.toByteArray(), "WordDocument");
            bos.reset();
            this.encryptBytes(tableBuf, this._fib.getFibBase().getLKey(), bos);
            HWPFDocument.write(pfs, bos.toByteArray(), "1Table");
            bos.reset();
            this.encryptBytes(dataBuf, 0, bos);
            HWPFDocument.write(pfs, bos.toByteArray(), STREAM_DATA);
            bos.reset();
        }
        this.writeProperties(pfs);
        if (copyOtherEntries && ei == null) {
            DirectoryNode newRoot = pfs.getRoot();
            this._objectPool.writeTo(newRoot);
            for (Entry entry : this.getDirectory()) {
                String entryName = entry.getName();
                if ("WordDocument".equals(entryName) || "0Table".equals(entryName) || "1Table".equals(entryName) || STREAM_DATA.equals(entryName) || "ObjectPool".equals(entryName) || "\u0005SummaryInformation".equals(entryName) || "\u0005DocumentSummaryInformation".equals(entryName)) continue;
                EntryUtils.copyNodeRecursively(entry, newRoot);
            }
        }
        this.replaceDirectory(pfs.getRoot());
        this._tableStream = tableStream.toByteArray();
        this._dataStream = dataBuf;
    }

    private void encryptBytes(byte[] plain, int encryptOffset, OutputStream bos) throws IOException {
        try {
            EncryptionInfo ei = this.getEncryptionInfo();
            Encryptor enc = ei.getEncryptor();
            enc.setChunkSize(512);
            ChunkedCipherOutputStream os = enc.getDataStream(bos, 0);
            if (encryptOffset > 0) {
                os.writePlain(plain, 0, encryptOffset);
            }
            os.write(plain, encryptOffset, plain.length - encryptOffset);
            os.close();
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    private static byte[] fillUp4096(byte[] buf) {
        if (buf == null) {
            return new byte[4096];
        }
        if (buf.length < 4096) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
            bos.write(buf, 0, buf.length);
            return HWPFDocument.fillUp4096(bos);
        }
        return buf;
    }

    private static byte[] fillUp4096(ByteArrayOutputStream bos) {
        int fillSize = 4096 - bos.size();
        if (fillSize > 0) {
            bos.write(new byte[fillSize], 0, fillSize);
        }
        return bos.toByteArray();
    }

    private static void write(POIFSFileSystem pfs, byte[] data, String name) throws IOException {
        pfs.createOrUpdateDocument(new ByteArrayInputStream(data), name);
    }

    @Internal
    public byte[] getDataStream() {
        return this._dataStream;
    }

    @Internal
    public byte[] getTableStream() {
        return this._tableStream;
    }

    public int registerList(HWPFList list) {
        if (this._lt == null) {
            this._lt = new ListTables();
        }
        return this._lt.addList(list.getListData(), list.getLFO(), list.getLFOData());
    }

    public void delete(int start, int length) {
        Range r = new Range(start, start + length, this);
        r.delete();
    }
}

