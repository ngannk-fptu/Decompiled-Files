/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class InputStreamDataSource
implements DataSource {
    private final String name;
    private final String contentType;
    private final InputStream inputStream;

    public InputStreamDataSource(String name, String contentType, InputStream inputStream) {
        this.name = name;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("This is not a writable data source.");
    }
}

