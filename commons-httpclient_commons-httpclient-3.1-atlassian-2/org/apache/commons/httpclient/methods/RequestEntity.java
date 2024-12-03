/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestEntity {
    public boolean isRepeatable();

    public void writeRequest(OutputStream var1) throws IOException;

    public long getContentLength();

    public String getContentType();
}

