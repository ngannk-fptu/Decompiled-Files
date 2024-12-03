/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.extractor;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.OldFormulaRecord;
import org.apache.poi.hssf.record.OldLabelRecord;
import org.apache.poi.hssf.record.OldSheetRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;

public class OldExcelExtractor
implements POITextExtractor {
    private static final int FILE_PASS_RECORD_SID = 47;
    private RecordInputStream ris;
    private Closeable toClose;
    private int biffVersion;
    private int fileType;

    public OldExcelExtractor(InputStream input) throws IOException {
        this.open(input);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OldExcelExtractor(File f) throws IOException {
        POIFSFileSystem poifs = null;
        try {
            poifs = new POIFSFileSystem(f);
            this.open(poifs);
            this.toClose = poifs;
            return;
        }
        catch (OldExcelFormatException | NotOLE2FileException exception) {
        }
        finally {
            if (this.toClose == null) {
                IOUtils.closeQuietly(poifs);
            }
        }
        FileInputStream biffStream = new FileInputStream(f);
        try {
            this.open(biffStream);
        }
        catch (IOException | RuntimeException e) {
            biffStream.close();
            this.toClose.close();
            throw e;
        }
    }

    public OldExcelExtractor(POIFSFileSystem fs) throws IOException {
        this.toClose = fs;
        this.open(fs);
    }

    public OldExcelExtractor(DirectoryNode directory) throws IOException {
        this.toClose = directory.getFileSystem();
        this.open(directory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void open(InputStream biffStream) throws IOException {
        BufferedInputStream bis;
        BufferedInputStream bufferedInputStream = bis = biffStream instanceof BufferedInputStream ? (BufferedInputStream)biffStream : new BufferedInputStream(biffStream, 8);
        if (FileMagic.valueOf(bis) == FileMagic.OLE2) {
            POIFSFileSystem poifs = new POIFSFileSystem(bis);
            try {
                this.open(poifs);
                this.toClose = poifs;
            }
            finally {
                if (this.toClose == null) {
                    poifs.close();
                }
            }
        } else {
            this.ris = new RecordInputStream(bis);
            this.toClose = bis;
            this.prepare();
        }
    }

    private void open(POIFSFileSystem fs) throws IOException {
        this.open(fs.getRoot());
    }

    private void open(DirectoryNode directory) throws IOException {
        DocumentNode book;
        try {
            book = (DocumentNode)directory.getEntry("Book");
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            book = (DocumentNode)directory.getEntry(InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES.get(0));
        }
        if (book == null) {
            throw new IOException("No Excel 5/95 Book stream found");
        }
        this.ris = new RecordInputStream(directory.createDocumentInputStream(book));
        this.prepare();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   OldExcelExtractor <filename>");
            System.exit(1);
        }
        try (OldExcelExtractor extractor = new OldExcelExtractor(new File(args[0]));){
            System.out.println(extractor.getText());
        }
    }

    private void prepare() {
        if (!this.ris.hasNextRecord()) {
            throw new IllegalArgumentException("File contains no records!");
        }
        this.ris.nextRecord();
        short bofSid = this.ris.getSid();
        switch (bofSid) {
            case 9: {
                this.biffVersion = 2;
                break;
            }
            case 521: {
                this.biffVersion = 3;
                break;
            }
            case 1033: {
                this.biffVersion = 4;
                break;
            }
            case 2057: {
                this.biffVersion = 5;
                break;
            }
            default: {
                throw new IllegalArgumentException("File does not begin with a BOF, found sid of " + bofSid);
            }
        }
        BOFRecord bof = new BOFRecord(this.ris);
        this.fileType = bof.getType();
    }

    public int getBiffVersion() {
        return this.biffVersion;
    }

    public int getFileType() {
        return this.fileType;
    }

    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        CodepageRecord codepage = null;
        block10: while (this.ris.hasNextRecord()) {
            int sid = this.ris.getNextSid();
            this.ris.nextRecord();
            switch (sid) {
                case 47: {
                    throw new EncryptedDocumentException("Encryption not supported for Old Excel files");
                }
                case 133: {
                    OldSheetRecord shr = new OldSheetRecord(this.ris);
                    shr.setCodePage(codepage);
                    text.append("Sheet: ");
                    text.append(shr.getSheetname());
                    text.append('\n');
                    break;
                }
                case 4: 
                case 516: {
                    OldLabelRecord lr = new OldLabelRecord(this.ris);
                    lr.setCodePage(codepage);
                    text.append(lr.getValue());
                    text.append('\n');
                    break;
                }
                case 7: 
                case 519: {
                    OldStringRecord sr = new OldStringRecord(this.ris);
                    sr.setCodePage(codepage);
                    text.append(sr.getString());
                    text.append('\n');
                    break;
                }
                case 515: {
                    NumberRecord nr = new NumberRecord(this.ris);
                    this.handleNumericCell(text, nr.getValue());
                    break;
                }
                case 6: 
                case 518: 
                case 1030: {
                    GenericRecord fr;
                    if (this.biffVersion == 5) {
                        fr = new FormulaRecord(this.ris);
                        if (((FormulaRecord)fr).getCachedResultTypeEnum() != CellType.NUMERIC) continue block10;
                        this.handleNumericCell(text, ((FormulaRecord)fr).getValue());
                        break;
                    }
                    fr = new OldFormulaRecord(this.ris);
                    if (((OldFormulaRecord)fr).getCachedResultTypeEnum() != CellType.NUMERIC) continue block10;
                    this.handleNumericCell(text, ((OldFormulaRecord)fr).getValue());
                    break;
                }
                case 638: {
                    RKRecord rr = new RKRecord(this.ris);
                    this.handleNumericCell(text, rr.getRKNumber());
                    break;
                }
                case 66: {
                    codepage = new CodepageRecord(this.ris);
                    break;
                }
                default: {
                    this.ris.readFully(IOUtils.safelyAllocate(this.ris.remaining(), HSSFWorkbook.getMaxRecordLength()));
                }
            }
        }
        this.ris = null;
        return text.toString();
    }

    protected void handleNumericCell(StringBuilder text, double value) {
        text.append(value);
        text.append('\n');
    }

    @Override
    public POITextExtractor getMetadataTextExtractor() {
        return new POITextExtractor(){

            @Override
            public String getText() {
                return "";
            }

            @Override
            public POITextExtractor getMetadataTextExtractor() {
                throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
            }

            @Override
            public void setCloseFilesystem(boolean doCloseFilesystem) {
            }

            @Override
            public boolean isCloseFilesystem() {
                return OldExcelExtractor.this.toClose != null;
            }

            @Override
            public Closeable getFilesystem() {
                return OldExcelExtractor.this.toClose;
            }

            @Override
            public Object getDocument() {
                return OldExcelExtractor.this.ris;
            }
        };
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.toClose != null;
    }

    @Override
    public Closeable getFilesystem() {
        return this.toClose;
    }

    @Override
    public Object getDocument() {
        return this.ris;
    }
}

