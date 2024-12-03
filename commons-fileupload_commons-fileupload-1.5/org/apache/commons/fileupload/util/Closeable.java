/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload.util;

import java.io.IOException;

public interface Closeable {
    public void close() throws IOException;

    public boolean isClosed() throws IOException;
}

