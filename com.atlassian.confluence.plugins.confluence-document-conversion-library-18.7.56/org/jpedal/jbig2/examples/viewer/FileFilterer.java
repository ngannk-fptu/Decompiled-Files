/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.examples.viewer;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileFilterer
extends FileFilter {
    String[] extensions;
    String description;
    int items = 0;

    public FileFilterer(String[] stringArray, String string) {
        this.items = stringArray.length;
        this.extensions = new String[this.items];
        for (int i = 0; i < this.items; ++i) {
            this.extensions[i] = stringArray[i].toLowerCase();
            this.description = string;
        }
    }

    public final String getDescription() {
        return this.description;
    }

    public final boolean accept(File file) {
        boolean bl = false;
        if (file.isDirectory()) {
            bl = true;
        } else {
            String string = file.getName().toLowerCase();
            for (int i = 0; i < this.items; ++i) {
                if (!string.endsWith(this.extensions[i])) continue;
                bl = true;
            }
        }
        return bl;
    }
}

