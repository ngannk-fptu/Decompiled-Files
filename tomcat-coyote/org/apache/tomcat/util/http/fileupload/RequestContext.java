/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;

public interface RequestContext {
    public String getCharacterEncoding();

    public String getContentType();

    public InputStream getInputStream() throws IOException;
}

