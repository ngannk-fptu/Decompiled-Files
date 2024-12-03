/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.classreading.SimpleMetadataReaderFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.type.classreading;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.data.type.classreading.DefaultMethodsMetadataReader;
import org.springframework.data.type.classreading.MethodsMetadataReader;
import org.springframework.lang.Nullable;

public class MethodsMetadataReaderFactory
extends SimpleMetadataReaderFactory {
    public MethodsMetadataReaderFactory() {
    }

    public MethodsMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    public MethodsMetadataReaderFactory(@Nullable ClassLoader classLoader) {
        super(classLoader);
    }

    public MethodsMetadataReader getMetadataReader(String className) throws IOException {
        return (MethodsMetadataReader)super.getMetadataReader(className);
    }

    public MethodsMetadataReader getMetadataReader(Resource resource) throws IOException {
        return new DefaultMethodsMetadataReader(resource, this.getResourceLoader().getClassLoader());
    }
}

