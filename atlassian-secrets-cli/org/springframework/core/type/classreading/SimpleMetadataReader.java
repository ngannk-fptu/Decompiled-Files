/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type.classreading;

import java.io.BufferedInputStream;
import java.io.IOException;
import org.springframework.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;

final class SimpleMetadataReader
implements MetadataReader {
    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        ClassReader classReader;
        try (BufferedInputStream is = new BufferedInputStream(resource.getInputStream());){
            classReader = new ClassReader(is);
        }
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
        classReader.accept(visitor, 2);
        this.annotationMetadata = visitor;
        this.classMetadata = visitor;
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return this.classMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}

