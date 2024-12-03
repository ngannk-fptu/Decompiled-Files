/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.filesystem;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.nio.FileBackedDataSource;
import org.apache.poi.util.TempFile;

public class TempFilePOIFSFileSystem
extends POIFSFileSystem {
    private static Logger LOG = LogManager.getLogger(TempFilePOIFSFileSystem.class);
    File tempFile;

    @Override
    protected void createNewDataSource() {
        try {
            this.tempFile = TempFile.createTempFile("poifs", ".tmp");
            this._data = new FileBackedDataSource(this.tempFile, false);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create data source", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.tempFile != null && this.tempFile.exists() && !this.tempFile.delete()) {
            LOG.atDebug().log("temp file was already deleted (probably due to previous call to close this resource)");
        }
        super.close();
    }
}

