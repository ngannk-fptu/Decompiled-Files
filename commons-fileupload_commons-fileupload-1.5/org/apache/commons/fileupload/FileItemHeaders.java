/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.util.Iterator;

public interface FileItemHeaders {
    public String getHeader(String var1);

    public Iterator<String> getHeaders(String var1);

    public Iterator<String> getHeaderNames();
}

