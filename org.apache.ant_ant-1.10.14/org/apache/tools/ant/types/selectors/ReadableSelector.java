/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.selectors.FileSelector;

public class ReadableSelector
implements FileSelector {
    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        return file != null && file.canRead();
    }
}

