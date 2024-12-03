/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public abstract class AbstractClassTestingTypeFilter
implements TypeFilter {
    @Override
    public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return this.match(metadataReader.getClassMetadata());
    }

    protected abstract boolean match(ClassMetadata var1);
}

