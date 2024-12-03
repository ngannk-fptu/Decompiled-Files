/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util.autodetect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fontbox.util.autodetect.FontDirFinder;

public abstract class NativeFontDirFinder
implements FontDirFinder {
    @Override
    public List<File> find() {
        ArrayList<File> fontDirList = new ArrayList<File>();
        String[] searchableDirectories = this.getSearchableDirectories();
        if (searchableDirectories != null) {
            for (String searchableDirectorie : searchableDirectories) {
                File fontDir = new File(searchableDirectorie);
                try {
                    if (!fontDir.exists() || !fontDir.canRead()) continue;
                    fontDirList.add(fontDir);
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
        }
        return fontDirList;
    }

    protected abstract String[] getSearchableDirectories();
}

