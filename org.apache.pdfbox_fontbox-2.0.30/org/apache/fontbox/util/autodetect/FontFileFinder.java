/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.util.autodetect;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.util.autodetect.FontDirFinder;
import org.apache.fontbox.util.autodetect.MacFontDirFinder;
import org.apache.fontbox.util.autodetect.OS400FontDirFinder;
import org.apache.fontbox.util.autodetect.UnixFontDirFinder;
import org.apache.fontbox.util.autodetect.WindowsFontDirFinder;

public class FontFileFinder {
    private static final Log LOG = LogFactory.getLog(FontFileFinder.class);
    private FontDirFinder fontDirFinder = null;

    private FontDirFinder determineDirFinder() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return new WindowsFontDirFinder();
        }
        if (osName.startsWith("Mac")) {
            return new MacFontDirFinder();
        }
        if (osName.startsWith("OS/400")) {
            return new OS400FontDirFinder();
        }
        return new UnixFontDirFinder();
    }

    public List<URI> find() {
        if (this.fontDirFinder == null) {
            this.fontDirFinder = this.determineDirFinder();
        }
        List<File> fontDirs = this.fontDirFinder.find();
        ArrayList<URI> results = new ArrayList<URI>();
        for (File dir : fontDirs) {
            this.walk(dir, results);
        }
        return results;
    }

    public List<URI> find(String dir) {
        ArrayList<URI> results = new ArrayList<URI>();
        File directory = new File(dir);
        if (directory.isDirectory()) {
            this.walk(directory, results);
        }
        return results;
    }

    private void walk(File directory, List<URI> results) {
        if (!directory.isDirectory()) {
            return;
        }
        File[] filelist = directory.listFiles();
        if (filelist == null) {
            return;
        }
        for (File file : filelist) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(".")) continue;
                this.walk(file, results);
                continue;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("checkFontfile check " + file));
            }
            if (!this.checkFontfile(file)) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("checkFontfile found " + file));
            }
            results.add(file.toURI());
        }
    }

    private boolean checkFontfile(File file) {
        String name = file.getName().toLowerCase(Locale.US);
        return (name.endsWith(".ttf") || name.endsWith(".otf") || name.endsWith(".pfb") || name.endsWith(".ttc")) && !name.startsWith("fonts.");
    }
}

