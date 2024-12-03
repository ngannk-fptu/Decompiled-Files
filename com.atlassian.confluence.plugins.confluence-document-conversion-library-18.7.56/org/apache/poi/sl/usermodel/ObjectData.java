/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;

public interface ObjectData {
    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;

    default public byte[] getBytes() throws IOException {
        try (InputStream is = this.getInputStream();){
            byte[] byArray = IOUtils.toByteArray(is);
            return byArray;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    default public boolean hasDirectoryEntry() {
        try (InputStream is = FileMagic.prepareToCheckMagic(this.getInputStream());){
            FileMagic fm = FileMagic.valueOf(is);
            boolean bl = fm == FileMagic.OLE2;
            return bl;
        }
        catch (IOException e) {
            Logger LOG = LogManager.getLogger(ObjectData.class);
            LOG.atWarn().withThrowable(e).log("Can't determine filemagic of ole stream");
            return false;
        }
    }

    default public DirectoryEntry getDirectory() throws IOException {
        try (InputStream is = this.getInputStream();){
            DirectoryNode directoryNode = new POIFSFileSystem(is).getRoot();
            return directoryNode;
        }
    }

    public String getOLE2ClassName();

    public String getFileName();
}

