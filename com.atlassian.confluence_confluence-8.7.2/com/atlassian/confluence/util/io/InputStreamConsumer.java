/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamConsumer<T> {
    public T withInputStream(InputStream var1) throws IOException;
}

