/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.extract.binary;

import com.atlassian.confluence.plugins.conversion.extract.binary.AbstractBinaryExtractor;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class SpreadsheetBinaryExtractor
extends AbstractBinaryExtractor {
    public static String extractText(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder(1024);
        try (POIFSFileSystem poifs = new POIFSFileSystem(inputStream);
             DocumentInputStream din = poifs.createDocumentInputStream("Workbook");){
            HSSFRequest req = new HSSFRequest();
            req.addListenerForAllRecords(new ExcelEventListener(content));
            HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents(req, din);
        }
        catch (Exception e) {
            throw new IOException("Error reading content of Excel document: " + e.getMessage(), e);
        }
        return content.toString();
    }

    private static class ExcelEventListener
    implements HSSFListener {
        private final StringBuilder buff;
        private SSTRecord sstrec;
        private static final char SPACE = ' ';

        public ExcelEventListener(StringBuilder buff) {
            this.buff = buff;
        }

        @Override
        public void processRecord(Record record) {
            switch (record.getSid()) {
                case 515: {
                    NumberRecord numrec = (NumberRecord)record;
                    double numberValue = numrec.getValue();
                    if (this.isInteger(numberValue)) {
                        this.buff.append((int)numberValue).append(' ');
                        break;
                    }
                    this.buff.append(numberValue).append(' ');
                    break;
                }
                case 252: {
                    this.sstrec = (SSTRecord)record;
                    break;
                }
                case 253: {
                    LabelSSTRecord lrec = (LabelSSTRecord)record;
                    this.buff.append(this.sstrec.getString(lrec.getSSTIndex())).append(' ');
                }
            }
        }

        private boolean isInteger(double doubleValue) {
            double floored = Math.floor(doubleValue);
            return doubleValue - floored == 0.0;
        }
    }
}

