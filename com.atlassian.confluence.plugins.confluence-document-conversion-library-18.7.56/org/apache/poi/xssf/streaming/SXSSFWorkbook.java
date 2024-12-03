/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.zip.Zip64Mode
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.xssf.streaming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.CellReferenceType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.Removal;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.GZIPSheetDataWriter;
import org.apache.poi.xssf.streaming.OpcZipArchiveOutputStream;
import org.apache.poi.xssf.streaming.SXSSFCreationHelper;
import org.apache.poi.xssf.streaming.SXSSFEvaluationWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.apache.poi.xssf.usermodel.XSSFChartSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SXSSFWorkbook
implements Workbook {
    public static final int DEFAULT_WINDOW_SIZE = 100;
    private static final Logger LOG = LogManager.getLogger(SXSSFWorkbook.class);
    protected final XSSFWorkbook _wb;
    private final Map<SXSSFSheet, XSSFSheet> _sxFromXHash = new HashMap<SXSSFSheet, XSSFSheet>();
    private final Map<XSSFSheet, SXSSFSheet> _xFromSxHash = new HashMap<XSSFSheet, SXSSFSheet>();
    private int _randomAccessWindowSize = 100;
    private boolean _compressTmpFiles;
    protected final SharedStringsTable _sharedStringSource;
    protected Zip64Mode zip64Mode = Zip64Mode.Always;
    private boolean shouldCalculateSheetDimensions = true;

    public SXSSFWorkbook() {
        this(null);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook) {
        this(workbook, 100);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize) {
        this(workbook, rowAccessWindowSize, false);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize, boolean compressTmpFiles) {
        this(workbook, rowAccessWindowSize, compressTmpFiles, false);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable) {
        this.setRandomAccessWindowSize(rowAccessWindowSize);
        this.setCompressTempFiles(compressTmpFiles);
        if (workbook == null) {
            this._wb = new XSSFWorkbook();
            this._sharedStringSource = useSharedStringsTable ? this._wb.getSharedStringSource() : null;
        } else {
            this._wb = workbook;
            this._sharedStringSource = useSharedStringsTable ? this._wb.getSharedStringSource() : null;
            for (Sheet sheet : this._wb) {
                this.createAndRegisterSXSSFSheet((XSSFSheet)sheet);
            }
        }
    }

    public SXSSFWorkbook(int rowAccessWindowSize) {
        this(null, rowAccessWindowSize);
    }

    public int getRandomAccessWindowSize() {
        return this._randomAccessWindowSize;
    }

    protected void setRandomAccessWindowSize(int rowAccessWindowSize) {
        if (rowAccessWindowSize == 0 || rowAccessWindowSize < -1) {
            throw new IllegalArgumentException("rowAccessWindowSize must be greater than 0 or -1");
        }
        this._randomAccessWindowSize = rowAccessWindowSize;
    }

    public void setZip64Mode(Zip64Mode zip64Mode) {
        this.zip64Mode = zip64Mode;
    }

    public boolean isCompressTempFiles() {
        return this._compressTmpFiles;
    }

    public void setCompressTempFiles(boolean compress) {
        this._compressTmpFiles = compress;
    }

    public void setShouldCalculateSheetDimensions(boolean shouldCalculateSheetDimensions) {
        this.shouldCalculateSheetDimensions = shouldCalculateSheetDimensions;
    }

    public boolean shouldCalculateSheetDimensions() {
        return this.shouldCalculateSheetDimensions;
    }

    @Internal
    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }

    protected SheetDataWriter createSheetDataWriter() throws IOException {
        if (this._compressTmpFiles) {
            return new GZIPSheetDataWriter(this._sharedStringSource);
        }
        return new SheetDataWriter(this._sharedStringSource);
    }

    XSSFSheet getXSSFSheet(SXSSFSheet sheet) {
        return this._sxFromXHash.get(sheet);
    }

    SXSSFSheet getSXSSFSheet(XSSFSheet sheet) {
        return this._xFromSxHash.get(sheet);
    }

    void registerSheetMapping(SXSSFSheet sxSheet, XSSFSheet xSheet) {
        this._sxFromXHash.put(sxSheet, xSheet);
        this._xFromSxHash.put(xSheet, sxSheet);
    }

    void deregisterSheetMapping(XSSFSheet xSheet) {
        SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        if (sxSheet != null) {
            IOUtils.closeQuietly(sxSheet.getSheetDataWriter());
            this._sxFromXHash.remove(sxSheet);
            this._xFromSxHash.remove(xSheet);
        }
    }

    protected XSSFSheet getSheetFromZipEntryName(String sheetRef) {
        for (XSSFSheet sheet : this._sxFromXHash.values()) {
            if (!sheetRef.equals(sheet.getPackagePart().getPartName().getName().substring(1))) continue;
            return sheet;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void injectData(ZipEntrySource zipEntrySource, OutputStream out) throws IOException {
        ZipArchiveOutputStream zos = this.createArchiveOutputStream(out);
        try {
            Enumeration<? extends ZipArchiveEntry> en = zipEntrySource.getEntries();
            while (en.hasMoreElements()) {
                ZipArchiveEntry ze = en.nextElement();
                ZipArchiveEntry zeOut = new ZipArchiveEntry(ze.getName());
                if (ze.getSize() >= 0L) {
                    zeOut.setSize(ze.getSize());
                }
                if (ze.getTime() >= 0L) {
                    zeOut.setTime(ze.getTime());
                }
                zos.putArchiveEntry((ArchiveEntry)zeOut);
                try {
                    InputStream is = zipEntrySource.getInputStream(ze);
                    Throwable throwable = null;
                    try {
                        XSSFSheet xSheet;
                        if (is instanceof ZipArchiveThresholdInputStream) {
                            ((ZipArchiveThresholdInputStream)is).setGuardState(false);
                        }
                        if ((xSheet = this.getSheetFromZipEntryName(ze.getName())) != null && !(xSheet instanceof XSSFChartSheet)) {
                            SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
                            SXSSFWorkbook.copyStreamAndInjectWorksheet(is, (OutputStream)zos, this.createSheetInjector(sxSheet));
                            continue;
                        }
                        IOUtils.copy(is, (OutputStream)zos);
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        if (is == null) continue;
                        if (throwable != null) {
                            try {
                                is.close();
                            }
                            catch (Throwable throwable3) {
                                throwable.addSuppressed(throwable3);
                            }
                            continue;
                        }
                        is.close();
                    }
                }
                finally {
                    zos.closeArchiveEntry();
                }
            }
        }
        finally {
            zos.finish();
            zipEntrySource.close();
        }
    }

    protected ZipArchiveOutputStream createArchiveOutputStream(OutputStream out) {
        if (Zip64Mode.Always.equals((Object)this.zip64Mode)) {
            return new OpcZipArchiveOutputStream(out);
        }
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(out);
        zos.setUseZip64(this.zip64Mode);
        return zos;
    }

    protected ISheetInjector createSheetInjector(SXSSFSheet sxSheet) throws IOException {
        return output -> {
            try (InputStream xis = sxSheet.getWorksheetXMLInputStream();){
                IOUtils.copy(xis, output);
            }
        };
    }

    private static void copyStreamAndInjectWorksheet(InputStream in, OutputStream out, ISheetInjector sheetInjector) throws IOException {
        int c;
        InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        OutputStreamWriter outWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        boolean needsStartTag = true;
        int pos = 0;
        String s = "<sheetData";
        int n = s.length();
        while ((c = inReader.read()) != -1) {
            if (c == s.charAt(pos)) {
                if (++pos != n) continue;
                if (!"<sheetData".equals(s)) break;
                c = inReader.read();
                if (c == -1) {
                    outWriter.write(s);
                    break;
                }
                if (c == 62) {
                    outWriter.write(s);
                    outWriter.write(c);
                    s = "</sheetData>";
                    n = s.length();
                    pos = 0;
                    needsStartTag = false;
                    continue;
                }
                if (c == 47) {
                    c = inReader.read();
                    if (c == -1) {
                        outWriter.write(s);
                        break;
                    }
                    if (c == 62) break;
                    outWriter.write(s);
                    outWriter.write(47);
                    outWriter.write(c);
                    pos = 0;
                    continue;
                }
                outWriter.write(s);
                outWriter.write(47);
                outWriter.write(c);
                pos = 0;
                continue;
            }
            if (pos > 0) {
                outWriter.write(s, 0, pos);
            }
            if (c == s.charAt(0)) {
                pos = 1;
                continue;
            }
            outWriter.write(c);
            pos = 0;
        }
        outWriter.flush();
        if (needsStartTag) {
            outWriter.write("<sheetData>\n");
            outWriter.flush();
        }
        sheetInjector.writeSheetData(out);
        outWriter.write("</sheetData>");
        outWriter.flush();
        while ((c = inReader.read()) != -1) {
            outWriter.write(c);
        }
        outWriter.flush();
    }

    public XSSFWorkbook getXSSFWorkbook() {
        return this._wb;
    }

    @Override
    public int getActiveSheetIndex() {
        return this._wb.getActiveSheetIndex();
    }

    @Override
    public void setActiveSheet(int sheetIndex) {
        this._wb.setActiveSheet(sheetIndex);
    }

    @Override
    public int getFirstVisibleTab() {
        return this._wb.getFirstVisibleTab();
    }

    @Override
    public void setFirstVisibleTab(int sheetIndex) {
        this._wb.setFirstVisibleTab(sheetIndex);
    }

    @Override
    public void setSheetOrder(String sheetname, int pos) {
        this._wb.setSheetOrder(sheetname, pos);
    }

    @Override
    public void setSelectedTab(int index) {
        this._wb.setSelectedTab(index);
    }

    @Override
    public void setSheetName(int sheet, String name) {
        this._wb.setSheetName(sheet, name);
    }

    @Override
    public String getSheetName(int sheet) {
        return this._wb.getSheetName(sheet);
    }

    @Override
    public int getSheetIndex(String name) {
        return this._wb.getSheetIndex(name);
    }

    @Override
    public int getSheetIndex(Sheet sheet) {
        return this._wb.getSheetIndex(this.getXSSFSheet((SXSSFSheet)sheet));
    }

    @Override
    public SXSSFSheet createSheet() {
        return this.createAndRegisterSXSSFSheet(this._wb.createSheet());
    }

    SXSSFSheet createAndRegisterSXSSFSheet(XSSFSheet xSheet) {
        SXSSFSheet sxSheet;
        try {
            sxSheet = new SXSSFSheet(this, xSheet);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        this.registerSheetMapping(sxSheet, xSheet);
        return sxSheet;
    }

    @Override
    public SXSSFSheet createSheet(String sheetname) {
        return this.createAndRegisterSXSSFSheet(this._wb.createSheet(sheetname));
    }

    @Override
    @NotImplemented
    public Sheet cloneSheet(int sheetNum) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int getNumberOfSheets() {
        return this._wb.getNumberOfSheets();
    }

    @Override
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator<Sheet>();
    }

    @Override
    public Spliterator<Sheet> spliterator() {
        return this._wb.spliterator();
    }

    @Override
    public SXSSFSheet getSheetAt(int index) {
        return this.getSXSSFSheet(this._wb.getSheetAt(index));
    }

    @Override
    public SXSSFSheet getSheet(String name) {
        return this.getSXSSFSheet(this._wb.getSheet(name));
    }

    @Override
    public void removeSheetAt(int index) {
        XSSFSheet xSheet = this._wb.getSheetAt(index);
        SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        this._wb.removeSheetAt(index);
        this.deregisterSheetMapping(xSheet);
        try {
            sxSheet.dispose();
        }
        catch (IOException e) {
            LOG.atWarn().withThrowable(e).log("Failed to dispose old sheet");
        }
    }

    @Override
    public Font createFont() {
        return this._wb.createFont();
    }

    @Override
    public Font findFont(boolean bold, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline) {
        return this._wb.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
    }

    @Override
    public int getNumberOfFonts() {
        return this._wb.getNumberOfFonts();
    }

    @Override
    @Deprecated
    @Removal(version="6.0.0")
    public int getNumberOfFontsAsInt() {
        return this.getNumberOfFonts();
    }

    @Override
    public Font getFontAt(int idx) {
        return this._wb.getFontAt(idx);
    }

    @Override
    public CellStyle createCellStyle() {
        return this._wb.createCellStyle();
    }

    @Override
    public int getNumCellStyles() {
        return this._wb.getNumCellStyles();
    }

    @Override
    public CellStyle getCellStyleAt(int idx) {
        return this._wb.getCellStyleAt(idx);
    }

    @Override
    public void close() throws IOException {
        for (SXSSFSheet sheet : this._xFromSxHash.values()) {
            try {
                SheetDataWriter _writer = sheet.getSheetDataWriter();
                if (_writer == null) continue;
                _writer.close();
            }
            catch (IOException e) {
                LOG.atWarn().withThrowable(e).log("An exception occurred while closing sheet data writer for sheet {}.", (Object)sheet.getSheetName());
            }
        }
        this._wb.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream stream) throws IOException {
        boolean deleted;
        this.flushSheets();
        File tmplFile = TempFile.createTempFile("poi-sxssf-template", ".xlsx");
        try {
            try (FileOutputStream os = new FileOutputStream(tmplFile);){
                this._wb.write(os);
            }
            var5_4 = null;
            try (ZipSecureFile zf = new ZipSecureFile(tmplFile);
                 ZipFileZipEntrySource source = new ZipFileZipEntrySource(zf);){
                this.injectData(source, stream);
            }
            catch (Throwable throwable) {
                var5_4 = throwable;
                throw throwable;
            }
        }
        finally {
            deleted = tmplFile.delete();
        }
        if (!deleted) {
            throw new IOException("Could not delete temporary file after processing: " + tmplFile);
        }
    }

    public void writeAvoidingTempFiles(OutputStream stream) throws IOException {
        this.flushSheets();
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
            this._wb.write((OutputStream)bos);
            try (InputStream is = bos.toInputStream();
                 ZipInputStreamZipEntrySource source = new ZipInputStreamZipEntrySource(new ZipArchiveThresholdInputStream((InputStream)new ZipArchiveInputStream(is)));){
                this.injectData(source, stream);
            }
        }
    }

    protected void flushSheets() throws IOException {
        for (SXSSFSheet sheet : this._xFromSxHash.values()) {
            sheet.deriveDimension();
            sheet.flushRows();
        }
    }

    public boolean dispose() {
        boolean success = true;
        for (SXSSFSheet sheet : this._sxFromXHash.keySet()) {
            try {
                success = sheet.dispose() && success;
            }
            catch (IOException e) {
                LOG.atWarn().withThrowable(e).log("Failed to dispose sheet");
                success = false;
            }
        }
        return success;
    }

    @Override
    public int getNumberOfNames() {
        return this._wb.getNumberOfNames();
    }

    @Override
    public Name getName(String name) {
        return this._wb.getName(name);
    }

    @Override
    public List<? extends Name> getNames(String name) {
        return this._wb.getNames(name);
    }

    @Override
    public List<? extends Name> getAllNames() {
        return this._wb.getAllNames();
    }

    @Override
    public Name createName() {
        return this._wb.createName();
    }

    @Override
    public void removeName(Name name) {
        this._wb.removeName(name);
    }

    @Override
    public void setPrintArea(int sheetIndex, String reference) {
        this._wb.setPrintArea(sheetIndex, reference);
    }

    @Override
    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
        this._wb.setPrintArea(sheetIndex, startColumn, endColumn, startRow, endRow);
    }

    @Override
    public String getPrintArea(int sheetIndex) {
        return this._wb.getPrintArea(sheetIndex);
    }

    @Override
    public void removePrintArea(int sheetIndex) {
        this._wb.removePrintArea(sheetIndex);
    }

    @Override
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this._wb.getMissingCellPolicy();
    }

    @Override
    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy) {
        this._wb.setMissingCellPolicy(missingCellPolicy);
    }

    @Override
    public DataFormat createDataFormat() {
        return this._wb.createDataFormat();
    }

    @Override
    public int addPicture(byte[] pictureData, int format) {
        return this._wb.addPicture(pictureData, format);
    }

    @Override
    public List<? extends PictureData> getAllPictures() {
        return this._wb.getAllPictures();
    }

    @Override
    public CreationHelper getCreationHelper() {
        return new SXSSFCreationHelper(this);
    }

    protected boolean isDate1904() {
        return this._wb.isDate1904();
    }

    @Override
    @NotImplemented(value="XSSFWorkbook#isHidden is not implemented")
    public boolean isHidden() {
        return this._wb.isHidden();
    }

    @Override
    @NotImplemented(value="XSSFWorkbook#setHidden is not implemented")
    public void setHidden(boolean hiddenFlag) {
        this._wb.setHidden(hiddenFlag);
    }

    @Override
    public boolean isSheetHidden(int sheetIx) {
        return this._wb.isSheetHidden(sheetIx);
    }

    @Override
    public boolean isSheetVeryHidden(int sheetIx) {
        return this._wb.isSheetVeryHidden(sheetIx);
    }

    @Override
    public SheetVisibility getSheetVisibility(int sheetIx) {
        return this._wb.getSheetVisibility(sheetIx);
    }

    @Override
    public void setSheetHidden(int sheetIx, boolean hidden) {
        this._wb.setSheetHidden(sheetIx, hidden);
    }

    @Override
    public void setSheetVisibility(int sheetIx, SheetVisibility visibility) {
        this._wb.setSheetVisibility(sheetIx, visibility);
    }

    @Override
    @NotImplemented
    public int linkExternalWorkbook(String name, Workbook workbook) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void addToolPack(UDFFinder toolpack) {
        this._wb.addToolPack(toolpack);
    }

    @Override
    public void setForceFormulaRecalculation(boolean value) {
        this._wb.setForceFormulaRecalculation(value);
    }

    @Override
    public boolean getForceFormulaRecalculation() {
        return this._wb.getForceFormulaRecalculation();
    }

    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }

    @Override
    public int addOlePackage(byte[] oleData, String label, String fileName, String command) throws IOException {
        return this._wb.addOlePackage(oleData, label, fileName, command);
    }

    @Override
    public EvaluationWorkbook createEvaluationWorkbook() {
        return SXSSFEvaluationWorkbook.create(this);
    }

    @Override
    public CellReferenceType getCellReferenceType() {
        return this.getXSSFWorkbook().getCellReferenceType();
    }

    @Override
    public void setCellReferenceType(CellReferenceType cellReferenceType) {
        this.getXSSFWorkbook().setCellReferenceType(cellReferenceType);
    }

    protected final class SheetIterator<T extends Sheet>
    implements Iterator<T> {
        private final Iterator<XSSFSheet> it;

        public SheetIterator() {
            this.it = SXSSFWorkbook.this._wb.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override
        public T next() throws NoSuchElementException {
            XSSFSheet xssfSheet = this.it.next();
            return (T)SXSSFWorkbook.this.getSXSSFSheet(xssfSheet);
        }

        @Override
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on XSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }

    protected static interface ISheetInjector {
        public void writeSheetData(OutputStream var1) throws IOException;
    }
}

