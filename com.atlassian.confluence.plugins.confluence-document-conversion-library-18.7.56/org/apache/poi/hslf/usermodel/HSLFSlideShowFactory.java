/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.SlideShowProvider;
import org.apache.poi.util.Internal;

@Internal
public class HSLFSlideShowFactory
implements SlideShowProvider<HSLFShape, HSLFTextParagraph> {
    @Override
    public boolean accepts(FileMagic fm) {
        return FileMagic.OLE2 == fm;
    }

    public HSLFSlideShow create() {
        return new HSLFSlideShow();
    }

    public static HSLFSlideShow createSlideShow(POIFSFileSystem fs) throws IOException {
        return new HSLFSlideShow(fs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HSLFSlideShow create(DirectoryNode root, String password) throws IOException {
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            HSLFSlideShow hSLFSlideShow = new HSLFSlideShow(root);
            return hSLFSlideShow;
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }

    public HSLFSlideShow create(InputStream inp) throws IOException {
        return this.create(inp, null);
    }

    public HSLFSlideShow create(InputStream inp, String password) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(inp);
        return this.create(fs.getRoot(), password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HSLFSlideShow create(File file, String password, boolean readOnly) throws IOException {
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            HSLFSlideShow hSLFSlideShow = new HSLFSlideShow(new POIFSFileSystem(file, readOnly));
            return hSLFSlideShow;
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }
}

