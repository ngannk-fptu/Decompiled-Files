/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.ClassReader
 *  org.springframework.asm.ClassVisitor
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.asm.Type
 *  org.springframework.core.NestedIOException
 *  org.springframework.core.io.Resource
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor
 *  org.springframework.core.type.classreading.MethodMetadataReadingVisitor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.type.classreading;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.MethodMetadataReadingVisitor;
import org.springframework.data.type.MethodsMetadata;
import org.springframework.data.type.classreading.MethodsMetadataReader;
import org.springframework.data.util.StreamUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

class DefaultMethodsMetadataReader
implements MethodsMetadataReader {
    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;
    private final MethodsMetadata methodsMetadata;

    DefaultMethodsMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        MethodsMetadataReadingVisitor visitor = new MethodsMetadataReadingVisitor(classLoader);
        DefaultMethodsMetadataReader.createClassReader(resource).accept((ClassVisitor)visitor, 2);
        this.resource = resource;
        this.classMetadata = visitor;
        this.annotationMetadata = visitor;
        this.methodsMetadata = visitor;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static ClassReader createClassReader(Resource resource) throws IOException {
        try (BufferedInputStream is = new BufferedInputStream(resource.getInputStream());){
            ClassReader classReader = new ClassReader((InputStream)is);
            return classReader;
        }
        catch (IllegalArgumentException ex) {
            throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, (Throwable)ex);
        }
    }

    public Resource getResource() {
        return this.resource;
    }

    public ClassMetadata getClassMetadata() {
        return this.classMetadata;
    }

    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }

    @Override
    public MethodsMetadata getMethodsMetadata() {
        return this.methodsMetadata;
    }

    private static class MethodsMetadataReadingVisitor
    extends AnnotationMetadataReadingVisitor
    implements MethodsMetadata {
        MethodsMetadataReadingVisitor(@Nullable ClassLoader classLoader) {
            super(classLoader);
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ((access & 0x40) != 0) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if (name.equals("<init>")) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            MethodMetadataReadingVisitor visitor = new MethodMetadataReadingVisitor(name, access, this.getClassName(), Type.getReturnType((String)desc).getClassName(), this.classLoader, this.methodMetadataSet);
            this.methodMetadataSet.add(visitor);
            return visitor;
        }

        @Override
        public Set<MethodMetadata> getMethods() {
            return Collections.unmodifiableSet(this.methodMetadataSet);
        }

        @Override
        public Set<MethodMetadata> getMethods(String name) {
            Assert.hasText((String)name, (String)"Method name must not be null or empty");
            return this.methodMetadataSet.stream().filter(it -> it.getMethodName().equals(name)).collect(StreamUtils.toUnmodifiableSet());
        }
    }
}

