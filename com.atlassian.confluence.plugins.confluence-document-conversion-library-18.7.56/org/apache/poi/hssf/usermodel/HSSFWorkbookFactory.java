/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookProvider;
import org.apache.poi.util.Internal;

@Internal
public class HSSFWorkbookFactory
implements WorkbookProvider {
    @Override
    public boolean accepts(FileMagic fm) {
        return FileMagic.OLE2 == fm;
    }

    @Override
    public HSSFWorkbook create() {
        return new HSSFWorkbook();
    }

    public static HSSFWorkbook createWorkbook(POIFSFileSystem fs) throws IOException {
        return new HSSFWorkbook(fs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HSSFWorkbook create(DirectoryNode root, String password) throws IOException {
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(root, true);
            return hSSFWorkbook;
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }

    @Override
    public Workbook create(InputStream inp) throws IOException {
        return this.create(inp, null);
    }

    @Override
    public Workbook create(InputStream inp, String password) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(inp);
        return this.create(fs.getRoot(), password);
    }

    @Override
    public Workbook create(File file, String password, boolean readOnly) throws IOException {
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(file, readOnly);
            try {
                HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(fs, true);
                return hSSFWorkbook;
            }
            catch (RuntimeException e) {
                fs.close();
                throw e;
            }
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }
}

