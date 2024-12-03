/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.config;

import com.atlassian.data.activeobjects.repository.config.ActiveObjectsRepositoryConfigExtension;
import com.atlassian.data.activeobjects.repository.config.EnableActiveObjectsRepositories;
import java.lang.annotation.Annotation;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class ActiveObjectsRepositoriesRegistrar
extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableActiveObjectsRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new ActiveObjectsRepositoryConfigExtension();
    }
}

