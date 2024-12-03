/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.io.ResourceLoader
 */
package org.springframework.data.repository.config;

import java.util.Collection;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;

public interface RepositoryConfigurationExtension {
    public String getModuleName();

    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(T var1, ResourceLoader var2, boolean var3);

    public String getDefaultNamedQueryLocation();

    public String getRepositoryFactoryBeanClassName();

    public void registerBeansForRoot(BeanDefinitionRegistry var1, RepositoryConfigurationSource var2);

    public void postProcess(BeanDefinitionBuilder var1, RepositoryConfigurationSource var2);

    public void postProcess(BeanDefinitionBuilder var1, AnnotationRepositoryConfigurationSource var2);

    public void postProcess(BeanDefinitionBuilder var1, XmlRepositoryConfigurationSource var2);
}

