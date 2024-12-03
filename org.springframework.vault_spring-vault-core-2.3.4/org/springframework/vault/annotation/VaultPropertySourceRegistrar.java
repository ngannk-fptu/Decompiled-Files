/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.MutablePropertySources
 *  org.springframework.core.env.PropertySource
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.annotation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.annotation.VaultPropertySources;
import org.springframework.vault.core.env.LeaseAwareVaultPropertySource;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.util.PropertyTransformer;
import org.springframework.vault.core.util.PropertyTransformers;

class VaultPropertySourceRegistrar
implements ImportBeanDefinitionRegistrar,
BeanFactoryPostProcessor,
EnvironmentAware {
    @Nullable
    private Environment environment;

    VaultPropertySourceRegistrar() {
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment env = (ConfigurableEnvironment)beanFactory.getBean(ConfigurableEnvironment.class);
        MutablePropertySources propertySources = env.getPropertySources();
        this.registerPropertySources(beanFactory.getBeansOfType(org.springframework.vault.core.env.VaultPropertySource.class).values(), propertySources);
        this.registerPropertySources(beanFactory.getBeansOfType(LeaseAwareVaultPropertySource.class).values(), propertySources);
    }

    private void registerPropertySources(Collection<? extends PropertySource<?>> propertySources, MutablePropertySources mutablePropertySources) {
        for (PropertySource<?> vaultPropertySource : propertySources) {
            if (mutablePropertySources.contains(vaultPropertySource.getName())) continue;
            mutablePropertySources.addLast(vaultPropertySource);
        }
    }

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull((Object)annotationMetadata, (String)"AnnotationMetadata must not be null");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null");
        if (!registry.isBeanNameInUse("VaultPropertySourceRegistrar")) {
            registry.registerBeanDefinition("VaultPropertySourceRegistrar", (BeanDefinition)BeanDefinitionBuilder.rootBeanDefinition(VaultPropertySourceRegistrar.class).setRole(2).getBeanDefinition());
        }
        Set<AnnotationAttributes> propertySources = VaultPropertySourceRegistrar.attributesForRepeatable(annotationMetadata, VaultPropertySources.class.getName(), VaultPropertySource.class.getName());
        int counter = 0;
        for (AnnotationAttributes propertySource : propertySources) {
            String[] paths = propertySource.getStringArray("value");
            String ref = propertySource.getString("vaultTemplateRef");
            String propertyNamePrefix = propertySource.getString("propertyNamePrefix");
            VaultPropertySource.Renewal renewal = (VaultPropertySource.Renewal)propertySource.getEnum("renewal");
            boolean ignoreSecretNotFound = propertySource.getBoolean("ignoreSecretNotFound");
            Assert.isTrue((paths.length > 0 ? 1 : 0) != 0, (String)"At least one @VaultPropertySource(value) location is required");
            Assert.hasText((String)ref, (String)"'vaultTemplateRef' in @EnableVaultPropertySource must not be empty");
            PropertyTransformer propertyTransformer = StringUtils.hasText((String)propertyNamePrefix) ? PropertyTransformers.propertyNamePrefix(propertyNamePrefix) : PropertyTransformers.noop();
            block1: for (String propertyPath : paths) {
                if (!StringUtils.hasText((String)propertyPath)) continue;
                AbstractBeanDefinition beanDefinition = this.createBeanDefinition(ref, renewal, propertyTransformer, ignoreSecretNotFound, this.potentiallyResolveRequiredPlaceholders(propertyPath));
                while (true) {
                    String beanName;
                    if (!registry.isBeanNameInUse(beanName = "vaultPropertySource#" + counter)) {
                        registry.registerBeanDefinition(beanName, (BeanDefinition)beanDefinition);
                        continue block1;
                    }
                    ++counter;
                }
            }
        }
    }

    private String potentiallyResolveRequiredPlaceholders(String expression) {
        return this.environment != null ? this.environment.resolveRequiredPlaceholders(expression) : expression;
    }

    private AbstractBeanDefinition createBeanDefinition(String ref, VaultPropertySource.Renewal renewal, PropertyTransformer propertyTransformer, boolean ignoreResourceNotFound, String propertyPath) {
        BeanDefinitionBuilder builder;
        if (this.isRenewable(renewal)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(LeaseAwareVaultPropertySource.class);
            RequestedSecret requestedSecret = renewal == VaultPropertySource.Renewal.ROTATE ? RequestedSecret.rotating(propertyPath) : RequestedSecret.renewable(propertyPath);
            builder.addConstructorArgValue((Object)propertyPath);
            builder.addConstructorArgReference("secretLeaseContainer");
            builder.addConstructorArgValue((Object)requestedSecret);
        } else {
            builder = BeanDefinitionBuilder.rootBeanDefinition(org.springframework.vault.core.env.VaultPropertySource.class);
            builder.addConstructorArgValue((Object)propertyPath);
            builder.addConstructorArgReference(ref);
            builder.addConstructorArgValue((Object)propertyPath);
        }
        builder.addConstructorArgValue((Object)propertyTransformer);
        builder.addConstructorArgValue((Object)ignoreResourceNotFound);
        builder.setRole(2);
        return builder.getBeanDefinition();
    }

    private boolean isRenewable(VaultPropertySource.Renewal renewal) {
        return renewal == VaultPropertySource.Renewal.RENEW || renewal == VaultPropertySource.Renewal.ROTATE;
    }

    static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, String containerClassName, String annotationClassName) {
        LinkedHashSet<AnnotationAttributes> result = new LinkedHashSet<AnnotationAttributes>();
        VaultPropertySourceRegistrar.addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName, false));
        Map container = metadata.getAnnotationAttributes(containerClassName, false);
        if (container != null && container.containsKey("value")) {
            for (Map containedAttributes : (Map[])container.get("value")) {
                VaultPropertySourceRegistrar.addAttributesIfNotNull(result, containedAttributes);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private static void addAttributesIfNotNull(Set<AnnotationAttributes> result, @Nullable Map<String, Object> attributes) {
        if (attributes != null) {
            result.add(AnnotationAttributes.fromMap(attributes));
        }
    }
}

