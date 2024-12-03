/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.store.ConversionStore
 */
package com.benryan.conversion;

import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.nio.file.Path;
import java.util.UUID;

public interface FilePathAwareConversionStore
extends ConversionStore {
    public Path getFilePath(UUID var1);
}

