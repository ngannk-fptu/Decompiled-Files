/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

public class FragmentMetadata {
    private final MetadataReaderFactory factory;

    public FragmentMetadata(MetadataReaderFactory factory) {
        this.factory = factory;
    }

    public Stream<String> getFragmentInterfaces(String interfaceName) {
        Assert.hasText((String)interfaceName, (String)"Interface name must not be null or empty!");
        return Arrays.stream(this.getClassMetadata(interfaceName).getInterfaceNames()).filter(this::isCandidate);
    }

    private boolean isCandidate(String interfaceName) {
        Assert.hasText((String)interfaceName, (String)"Interface name must not be null or empty!");
        AnnotationMetadata metadata = this.getAnnotationMetadata(interfaceName);
        return !metadata.hasAnnotation(NoRepositoryBean.class.getName());
    }

    private AnnotationMetadata getAnnotationMetadata(String className) {
        try {
            return this.factory.getMetadataReader(className).getAnnotationMetadata();
        }
        catch (IOException e) {
            throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), (Throwable)e);
        }
    }

    private ClassMetadata getClassMetadata(String className) {
        try {
            return this.factory.getMetadataReader(className).getClassMetadata();
        }
        catch (IOException e) {
            throw new BeanDefinitionStoreException(String.format("Cannot parse %s metadata.", className), (Throwable)e);
        }
    }
}

