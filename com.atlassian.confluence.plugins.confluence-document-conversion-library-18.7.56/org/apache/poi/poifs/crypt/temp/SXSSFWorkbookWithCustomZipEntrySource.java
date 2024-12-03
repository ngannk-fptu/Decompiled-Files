/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.temp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.poifs.crypt.temp.AesZipFileZipEntrySource;
import org.apache.poi.poifs.crypt.temp.EncryptedTempData;
import org.apache.poi.poifs.crypt.temp.SheetDataWriterWithDecorator;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SheetDataWriter;

public class SXSSFWorkbookWithCustomZipEntrySource
extends SXSSFWorkbook {
    private static final Logger LOG = LogManager.getLogger(SXSSFWorkbookWithCustomZipEntrySource.class);

    public SXSSFWorkbookWithCustomZipEntrySource() {
        super(20);
        this.setCompressTempFiles(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream stream) throws IOException {
        this.flushSheets();
        EncryptedTempData tempData = new EncryptedTempData();
        AesZipFileZipEntrySource source = null;
        try {
            try (OutputStream os = tempData.getOutputStream();){
                this.getXSSFWorkbook().write(os);
            }
            var5_5 = null;
            try (InputStream tempStream = tempData.getInputStream();){
                source = AesZipFileZipEntrySource.createZipEntrySource(tempStream);
            }
            catch (Throwable throwable) {
                var5_5 = throwable;
                throw throwable;
            }
            this.injectData(source, stream);
            tempData.dispose();
        }
        catch (Throwable throwable) {
            tempData.dispose();
            IOUtils.closeQuietly(source);
            throw throwable;
        }
        IOUtils.closeQuietly(source);
    }

    @Override
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        LOG.atInfo().log("isCompressTempFiles: {}", (Object)Unbox.box(this.isCompressTempFiles()));
        LOG.atInfo().log("SharedStringSource: {}", (Object)this.getSharedStringSource());
        return new SheetDataWriterWithDecorator();
    }
}

