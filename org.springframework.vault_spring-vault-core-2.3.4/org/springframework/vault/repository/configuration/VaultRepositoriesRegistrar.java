/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
 *  org.springframework.data.repository.config.RepositoryConfigurationExtension
 */
package org.springframework.vault.repository.configuration;

import java.lang.annotation.Annotation;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.vault.repository.configuration.EnableVaultRepositories;
import org.springframework.vault.repository.configuration.VaultRepositoryConfigurationExtension;

public class VaultRepositoriesRegistrar
extends RepositoryBeanDefinitionRegistrarSupport {
    protected Class<? extends Annotation> getAnnotation() {
        return EnableVaultRepositories.class;
    }

    protected RepositoryConfigurationExtension getExtension() {
        return new VaultRepositoryConfigurationExtension();
    }
}

