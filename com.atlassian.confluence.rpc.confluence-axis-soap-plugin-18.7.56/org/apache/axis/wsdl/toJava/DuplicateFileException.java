/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;

public class DuplicateFileException
extends IOException {
    private String filename = null;

    public DuplicateFileException(String message, String filename) {
        super(message);
        this.filename = filename;
    }

    public String getFileName() {
        return this.filename;
    }
}

