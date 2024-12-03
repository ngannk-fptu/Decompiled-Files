/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.streaming;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xssf.streaming.DeferredSXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.apache.poi.xssf.streaming.StreamingSheetWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DeferredSXSSFWorkbook
extends SXSSFWorkbook {
    private static final Logger LOG = LogManager.getLogger(DeferredSXSSFWorkbook.class);

    public DeferredSXSSFWorkbook() {
        this(null);
    }

    public DeferredSXSSFWorkbook(int rowAccessWindowSize) {
        this(null, rowAccessWindowSize);
    }

    public DeferredSXSSFWorkbook(XSSFWorkbook workbook) {
        this(workbook, 100);
    }

    public DeferredSXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize) {
        super(workbook, rowAccessWindowSize, false, false);
    }

    @Override
    @NotImplemented
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        throw new RuntimeException("Not supported by DeferredSXSSFWorkbook");
    }

    protected StreamingSheetWriter createSheetDataWriter(OutputStream out) throws IOException {
        return new StreamingSheetWriter(out);
    }

    @Override
    protected SXSSFWorkbook.ISheetInjector createSheetInjector(SXSSFSheet sxSheet) throws IOException {
        DeferredSXSSFSheet ssxSheet = (DeferredSXSSFSheet)sxSheet;
        return output -> ssxSheet.writeRows(output);
    }

    @Override
    SXSSFSheet createAndRegisterSXSSFSheet(XSSFSheet xSheet) {
        DeferredSXSSFSheet sxSheet;
        try {
            sxSheet = new DeferredSXSSFSheet(this, xSheet);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        this.registerSheetMapping(sxSheet, xSheet);
        return sxSheet;
    }

    @Override
    public DeferredSXSSFSheet createSheet() {
        return (DeferredSXSSFSheet)super.createSheet();
    }

    @Override
    public DeferredSXSSFSheet createSheet(String sheetname) {
        return (DeferredSXSSFSheet)super.createSheet(sheetname);
    }

    @Override
    public Iterator<Sheet> sheetIterator() {
        return new SXSSFWorkbook.SheetIterator<Sheet>();
    }

    public DeferredSXSSFSheet getStreamingSheetAt(int index) {
        XSSFSheet xSheet = this._wb.getSheetAt(index);
        SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        if (sxSheet == null && xSheet != null) {
            return (DeferredSXSSFSheet)this.createAndRegisterSXSSFSheet(xSheet);
        }
        return (DeferredSXSSFSheet)sxSheet;
    }

    public XSSFSheet getXSSFSheet(String name) {
        return this._wb.getSheet(name);
    }

    public DeferredSXSSFSheet getStreamingSheet(String name) {
        XSSFSheet xSheet = this._wb.getSheet(name);
        DeferredSXSSFSheet sxSheet = (DeferredSXSSFSheet)this.getSXSSFSheet(xSheet);
        if (sxSheet == null && xSheet != null) {
            return (DeferredSXSSFSheet)this.createAndRegisterSXSSFSheet(xSheet);
        }
        return sxSheet;
    }

    @Override
    public void removeSheetAt(int index) {
        XSSFSheet xSheet = this._wb.getSheetAt(index);
        SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        this._wb.removeSheetAt(index);
        if (sxSheet != null) {
            this.deregisterSheetMapping(xSheet);
            try {
                sxSheet.dispose();
            }
            catch (IOException e) {
                LOG.atWarn().withThrowable(e).log("Failed to cleanup old sheet");
            }
        }
    }
}

