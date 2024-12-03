/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi;

import java.io.File;

public class EmptyFileException
extends IllegalArgumentException {
    private static final long serialVersionUID = 1536449292174360166L;

    public EmptyFileException() {
        super("The supplied file was empty (zero bytes long)");
    }

    public EmptyFileException(File file) {
        super(file.exists() ? "The supplied file '" + file.getAbsolutePath() + "' was empty (zero bytes long)" : "The file '" + file.getAbsolutePath() + "' does not exist");
    }
}

