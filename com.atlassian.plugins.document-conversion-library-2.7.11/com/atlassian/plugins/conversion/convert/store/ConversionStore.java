/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.store;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public interface ConversionStore {
    public OutputStream createFile(UUID var1);

    public InputStream readFile(UUID var1);
}

