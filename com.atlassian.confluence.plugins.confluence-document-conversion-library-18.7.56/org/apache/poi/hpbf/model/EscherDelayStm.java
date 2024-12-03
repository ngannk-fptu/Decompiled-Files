/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model;

import java.io.IOException;
import org.apache.poi.hpbf.model.EscherPart;
import org.apache.poi.poifs.filesystem.DirectoryNode;

public final class EscherDelayStm
extends EscherPart {
    private static final String[] PATH = new String[]{"Escher", "EscherDelayStm"};

    public EscherDelayStm(DirectoryNode baseDir) throws IOException {
        super(baseDir, PATH);
    }
}

