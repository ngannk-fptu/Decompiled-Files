/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.xssf.streaming.DeferredSXSSFWorkbook;
import org.apache.poi.xssf.streaming.RowGeneratorFunction;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class DeferredSXSSFSheet
extends SXSSFSheet {
    private RowGeneratorFunction rowGenerator;

    public DeferredSXSSFSheet(DeferredSXSSFWorkbook workbook, XSSFSheet xSheet) throws IOException {
        super(workbook, xSheet, workbook.getRandomAccessWindowSize());
    }

    @Override
    public InputStream getWorksheetXMLInputStream() throws IOException {
        throw new RuntimeException("Not supported by DeferredSXSSFSheet");
    }

    public void setRowGenerator(RowGeneratorFunction rowGenerator) {
        this.rowGenerator = rowGenerator;
    }

    public void writeRows(OutputStream out) throws IOException {
        this._writer = ((DeferredSXSSFWorkbook)this._workbook).createSheetDataWriter(out);
        try {
            if (this.rowGenerator != null) {
                this.rowGenerator.generateRows(this);
            }
        }
        catch (Exception e) {
            throw new IOException("Error generating Excel rows", e);
        }
        finally {
            this.flushRows(0);
            this._writer.close();
            out.flush();
        }
    }
}

