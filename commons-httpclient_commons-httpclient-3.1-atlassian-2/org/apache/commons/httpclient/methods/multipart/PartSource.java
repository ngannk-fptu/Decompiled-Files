/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.IOException;
import java.io.InputStream;

public interface PartSource {
    public long getLength();

    public String getFileName();

    public InputStream createInputStream() throws IOException;
}

