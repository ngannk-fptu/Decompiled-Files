/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.nio.file.Files;
import org.apache.tools.ant.types.selectors.FileSelector;

public class SymlinkSelector
implements FileSelector {
    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        return file != null && Files.isSymbolicLink(file.toPath());
    }
}

