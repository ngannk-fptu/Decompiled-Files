/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.FileUtil
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

public final class WriterFileSuffixFilter
extends javax.swing.filechooser.FileFilter
implements FileFilter {
    private final String description;
    private Map<String, Boolean> knownSuffixes = new HashMap<String, Boolean>(32);

    public WriterFileSuffixFilter() {
        this("Images (all supported output formats)");
    }

    public WriterFileSuffixFilter(String string) {
        this.description = string;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String string = FileUtil.getExtension((File)file);
        return !StringUtil.isEmpty((String)string) && this.hasWriterForSuffix(string);
    }

    private boolean hasWriterForSuffix(String string) {
        if (this.knownSuffixes.get(string) == Boolean.TRUE) {
            return true;
        }
        try {
            Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix(string);
            if (iterator.hasNext()) {
                this.knownSuffixes.put(string, Boolean.TRUE);
                return true;
            }
            this.knownSuffixes.put(string, Boolean.FALSE);
            return false;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}

