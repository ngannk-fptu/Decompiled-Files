/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io;

import java.io.File;
import java.io.IOException;

public class FileExistsException
extends IOException {
    private static final long serialVersionUID = 1L;

    public FileExistsException() {
    }

    public FileExistsException(File file) {
        super("File " + file + " exists");
    }

    public FileExistsException(String message) {
        super(message);
    }
}

