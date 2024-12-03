/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.ExtractorProvider;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hssf.extractor.EventBasedExcelExtractor;
import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;

public class MainExtractorFactory
implements ExtractorProvider {
    @Override
    public boolean accepts(FileMagic fm) {
        return FileMagic.OLE2 == fm;
    }

    @Override
    public POITextExtractor create(File file, String password) throws IOException {
        return this.create(new POIFSFileSystem(file, true).getRoot(), password);
    }

    @Override
    public POITextExtractor create(InputStream inputStream, String password) throws IOException {
        return this.create(new POIFSFileSystem(inputStream).getRoot(), password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public POITextExtractor create(DirectoryNode poifsDir, String password) throws IOException {
        String oldPW = Biff8EncryptionKey.getCurrentUserPassword();
        try {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            for (String workbookName : InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
                if (!poifsDir.hasEntry(workbookName)) continue;
                ExcelExtractor excelExtractor = ExtractorFactory.getPreferEventExtractor() ? new EventBasedExcelExtractor(poifsDir) : new org.apache.poi.hssf.extractor.ExcelExtractor(poifsDir);
                return excelExtractor;
            }
            if (poifsDir.hasEntry("Book")) {
                OldExcelExtractor oldExcelExtractor = new OldExcelExtractor(poifsDir);
                return oldExcelExtractor;
            }
        }
        finally {
            Biff8EncryptionKey.setCurrentUserPassword(oldPW);
        }
        return null;
    }
}

