/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.util.Assert;

public class AnnotationRepositoryMetadata
extends AbstractRepositoryMetadata {
    private static final String NO_ANNOTATION_FOUND = String.format("Interface %%s must be annotated with @%s!", RepositoryDefinition.class.getName());
    private final Class<?> idType;
    private final Class<?> domainType;

    public AnnotationRepositoryMetadata(Class<?> repositoryInterface) {
        super(repositoryInterface);
        Assert.isTrue((boolean)repositoryInterface.isAnnotationPresent(RepositoryDefinition.class), (String)String.format(NO_ANNOTATION_FOUND, repositoryInterface.getName()));
        this.idType = this.resolveIdType(repositoryInterface);
        this.domainType = this.resolveDomainType(repositoryInterface);
    }

    @Override
    public Class<?> getIdType() {
        return this.idType;
    }

    @Override
    public Class<?> getDomainType() {
        return this.domainType;
    }

    private Class<?> resolveIdType(Class<?> repositoryInterface) {
        RepositoryDefinition annotation = repositoryInterface.getAnnotation(RepositoryDefinition.class);
        if (annotation == null || annotation.idClass() == null) {
            throw new IllegalArgumentException(String.format("Could not resolve id type of %s!", repositoryInterface));
        }
        return annotation.idClass();
    }

    private Class<?> resolveDomainType(Class<?> repositoryInterface) {
        RepositoryDefinition annotation = repositoryInterface.getAnnotation(RepositoryDefinition.class);
        if (annotation == null || annotation.domainClass() == null) {
            throw new IllegalArgumentException(String.format("Could not resolve domain type of %s!", repositoryInterface));
        }
        return annotation.domainClass();
    }
}

