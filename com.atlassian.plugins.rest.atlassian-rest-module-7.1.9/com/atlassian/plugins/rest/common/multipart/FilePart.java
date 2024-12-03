/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FilePart {
    public String getName();

    public String getContentType();

    public void write(File var1) throws IOException;

    public InputStream getInputStream() throws IOException;

    public String getValue();

    public boolean isFormField();

    public long getSize();
}

