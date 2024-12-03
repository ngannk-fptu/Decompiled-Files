/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.IOException;
import java.io.InputStream;

public interface RequestContext {
    public String getCharacterEncoding();

    public String getContentType();

    @Deprecated
    public int getContentLength();

    public InputStream getInputStream() throws IOException;
}

