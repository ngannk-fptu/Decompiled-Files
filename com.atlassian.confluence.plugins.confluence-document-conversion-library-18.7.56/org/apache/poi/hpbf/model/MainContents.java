/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model;

import java.io.IOException;
import org.apache.poi.hpbf.model.HPBFPart;
import org.apache.poi.poifs.filesystem.DirectoryNode;

public final class MainContents
extends HPBFPart {
    private static final String[] PATH = new String[]{"Contents"};

    public MainContents(DirectoryNode baseDir) throws IOException {
        super(baseDir, PATH);
    }

    @Override
    protected void generateData() {
    }
}

