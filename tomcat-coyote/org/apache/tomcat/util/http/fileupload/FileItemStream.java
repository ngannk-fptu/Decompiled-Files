/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport;

public interface FileItemStream
extends FileItemHeadersSupport {
    public InputStream openStream() throws IOException;

    public String getContentType();

    public String getName();

    public String getFieldName();

    public boolean isFormField();

    public static class ItemSkippedException
    extends IOException {
        private static final long serialVersionUID = -7280778431581963740L;
    }
}

