/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type.classreading;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;

public interface MetadataReaderFactory {
    public MetadataReader getMetadataReader(String var1) throws IOException;

    public MetadataReader getMetadataReader(Resource var1) throws IOException;
}

