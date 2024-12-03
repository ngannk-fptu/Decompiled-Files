/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.FileItemHeadersSupport;

public interface FileItem
extends FileItemHeadersSupport {
    public InputStream getInputStream() throws IOException;

    public String getContentType();

    public String getName();

    public boolean isInMemory();

    public long getSize();

    public byte[] get();

    public String getString(String var1) throws UnsupportedEncodingException;

    public String getString();

    public void write(File var1) throws Exception;

    public void delete();

    public String getFieldName();

    public void setFieldName(String var1);

    public boolean isFormField();

    public void setFormField(boolean var1);

    public OutputStream getOutputStream() throws IOException;
}

