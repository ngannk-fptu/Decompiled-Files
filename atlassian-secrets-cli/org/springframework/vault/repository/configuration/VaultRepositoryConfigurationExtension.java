/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension
 *  org.springframework.data.repository.config.RepositoryConfigurationSource
 */
package org.springframework.vault.repository.configuration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.vault.repository.core.VaultKeyValueAdapter;
import org.springframework.vault.repository.core.VaultKeyValueTemplate;
import org.springframework.vault.repository.mapping.Secret;
import org.springframework.vault.repository.mapping.VaultMappingContext;

public class VaultRepositoryConfigurationExtension
extends KeyValueRepositoryConfigurationExtension {
    private static final String VAULT_ADAPTER_BEAN_NAME = "vaultKeyValueAdapter";
    private static final String VAULT_MAPPING_CONTEXT_BEAN_NAME = "vaultMappingContext";

    public String getModuleName() {
        return "Vault";
    }

    protected String getModulePrefix() {
        return "vault";
    }

    protected String getDefaultKeyValueTemplateRef() {
        return "vaultKeyValueTemplate";
    }

    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
        Optional vaultTemplateRef = configurationSource.getAttribute("vaultTemplateRef");
        RootBeanDefinition mappingContextDefinition = this.createVaultMappingContext(configurationSource);
        mappingContextDefinition.setSource(configurationSource.getSource());
        VaultRepositoryConfigurationExtension.registerIfNotAlreadyRegistered(() -> mappingContextDefinition, (BeanDefinitionRegistry)registry, (String)VAULT_MAPPING_CONTEXT_BEAN_NAME, (Object)configurationSource);
        RootBeanDefinition vaultKeyValueAdapterDefinition = new RootBeanDefinition(VaultKeyValueAdapter.class);
        ConstructorArgumentValues constructorArgumentValuesForVaultKeyValueAdapter = new ConstructorArgumentValues();
        constructorArgumentValuesForVaultKeyValueAdapter.addIndexedArgumentValue(0, new RuntimeBeanReference(vaultTemplateRef.orElse("vaultTemplate")));
        vaultKeyValueAdapterDefinition.setConstructorArgumentValues(constructorArgumentValuesForVaultKeyValueAdapter);
        VaultRepositoryConfigurationExtension.registerIfNotAlreadyRegistered(() -> vaultKeyValueAdapterDefinition, (BeanDefinitionRegistry)registry, (String)VAULT_ADAPTER_BEAN_NAME, (Object)configurationSource);
        Optional keyValueTemplateName = configurationSource.getAttribute("keyValueTemplateRef");
        if (keyValueTemplateName.isPresent() && this.getDefaultKeyValueTemplateRef().equals(keyValueTemplateName.get()) && !registry.containsBeanDefinition((String)keyValueTemplateName.get())) {
            VaultRepositoryConfigurationExtension.registerIfNotAlreadyRegistered(() -> this.getDefaultKeyValueTemplateBeanDefinition(configurationSource), (BeanDefinitionRegistry)registry, (String)((String)keyValueTemplateName.get()), (Object)configurationSource.getSource());
        }
        super.registerBeansForRoot(registry, configurationSource);
    }

    private RootBeanDefinition createVaultMappingContext(RepositoryConfigurationSource configurationSource) {
        ConstructorArgumentValues mappingContextArgs = new ConstructorArgumentValues();
        RootBeanDefinition mappingContextBeanDef = new RootBeanDefinition(VaultMappingContext.class);
        mappingContextBeanDef.setConstructorArgumentValues(mappingContextArgs);
        mappingContextBeanDef.setSource(configurationSource.getSource());
        return mappingContextBeanDef;
    }

    protected AbstractBeanDefinition getDefaultKeyValueTemplateBeanDefinition(RepositoryConfigurationSource configurationSource) {
        RootBeanDefinition keyValueTemplateDefinition = new RootBeanDefinition(VaultKeyValueTemplate.class);
        ConstructorArgumentValues constructorArgumentValuesForKeyValueTemplate = new ConstructorArgumentValues();
        constructorArgumentValuesForKeyValueTemplate.addIndexedArgumentValue(0, new RuntimeBeanReference(VAULT_ADAPTER_BEAN_NAME));
        constructorArgumentValuesForKeyValueTemplate.addIndexedArgumentValue(1, new RuntimeBeanReference(VAULT_MAPPING_CONTEXT_BEAN_NAME));
        keyValueTemplateDefinition.setConstructorArgumentValues(constructorArgumentValuesForKeyValueTemplate);
        return keyValueTemplateDefinition;
    }

    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(Secret.class);
    }

    protected String getMappingContextBeanRef() {
        return VAULT_MAPPING_CONTEXT_BEAN_NAME;
    }
}

