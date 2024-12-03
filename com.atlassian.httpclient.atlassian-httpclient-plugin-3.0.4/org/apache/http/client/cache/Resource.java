/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public interface Resource
extends Serializable {
    public InputStream getInputStream() throws IOException;

    public long length();

    public void dispose();
}

