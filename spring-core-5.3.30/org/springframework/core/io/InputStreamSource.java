/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSource {
    public InputStream getInputStream() throws IOException;
}

