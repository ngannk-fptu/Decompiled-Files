/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import com.mchange.io.StringMemoryFile;
import com.mchange.io.impl.LazyReadOnlyMemoryFileImpl;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LazyStringMemoryFileImpl
extends LazyReadOnlyMemoryFileImpl
implements StringMemoryFile {
    private static final String DEFAULT_ENCODING;
    String encoding = null;
    String string = null;

    public LazyStringMemoryFileImpl(File file) {
        super(file);
    }

    public LazyStringMemoryFileImpl(String string) {
        super(string);
    }

    @Override
    public synchronized String asString(String string) throws IOException, UnsupportedEncodingException {
        this.update();
        if (this.encoding != string) {
            this.string = new String(this.bytes, string);
        }
        return this.string;
    }

    @Override
    public String asString() throws IOException {
        try {
            return this.asString(DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("Default Encoding is not supported?!");
        }
    }

    @Override
    void refreshBytes() throws IOException {
        super.refreshBytes();
        this.string = null;
        this.encoding = null;
    }

    static {
        String string = System.getProperty("file.encoding");
        DEFAULT_ENCODING = string == null ? "8859_1" : string;
    }
}

