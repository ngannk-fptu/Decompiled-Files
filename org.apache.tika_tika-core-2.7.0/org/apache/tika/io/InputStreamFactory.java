/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory {
    public InputStream getInputStream() throws IOException;
}

