/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.springframework.beans.BeanUtils
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.weaving.LoadTimeWeaverAware
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.instrument.classloading.LoadTimeWeaver
 *  org.springframework.jdbc.datasource.lookup.DataSourceLookup
 *  org.springframework.jdbc.datasource.lookup.SingleDataSourceLookup
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.orm.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.SingleDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class LocalContainerEntityManagerFactoryBean
extends AbstractEntityManagerFactoryBean
implements ResourceLoaderAware,
LoadTimeWeaverAware {
    @Nullable
    private PersistenceUnitManager persistenceUnitManager;
    private final DefaultPersistenceUnitManager internalPersistenceUnitManager = new DefaultPersistenceUnitManager();
    @Nullable
    private PersistenceUnitInfo persistenceUnitInfo;

    public void setPersistenceUnitManager(PersistenceUnitManager persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager;
    }

    public void setPersistenceXmlLocation(String persistenceXmlLocation) {
        this.internalPersistenceUnitManager.setPersistenceXmlLocation(persistenceXmlLocation);
    }

    @Override
    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        super.setPersistenceUnitName(persistenceUnitName);
        if (persistenceUnitName != null) {
            this.internalPersistenceUnitManager.setDefaultPersistenceUnitName(persistenceUnitName);
        }
    }

    public void setPersistenceUnitRootLocation(String defaultPersistenceUnitRootLocation) {
        this.internalPersistenceUnitManager.setDefaultPersistenceUnitRootLocation(defaultPersistenceUnitRootLocation);
    }

    public void setPackagesToScan(String ... packagesToScan) {
        this.internalPersistenceUnitManager.setPackagesToScan(packagesToScan);
    }

    public void setMappingResources(String ... mappingResources) {
        this.internalPersistenceUnitManager.setMappingResources(mappingResources);
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.internalPersistenceUnitManager.setSharedCacheMode(sharedCacheMode);
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.internalPersistenceUnitManager.setValidationMode(validationMode);
    }

    public void setDataSource(DataSource dataSource) {
        this.internalPersistenceUnitManager.setDataSourceLookup((DataSourceLookup)new SingleDataSourceLookup(dataSource));
        this.internalPersistenceUnitManager.setDefaultDataSource(dataSource);
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        this.internalPersistenceUnitManager.setDataSourceLookup((DataSourceLookup)new SingleDataSourceLookup(jtaDataSource));
        this.internalPersistenceUnitManager.setDefaultJtaDataSource(jtaDataSource);
    }

    public void setPersistenceUnitPostProcessors(PersistenceUnitPostProcessor ... postProcessors) {
        this.internalPersistenceUnitManager.setPersistenceUnitPostProcessors(postProcessors);
    }

    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.internalPersistenceUnitManager.setLoadTimeWeaver(loadTimeWeaver);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.internalPersistenceUnitManager.setResourceLoader(resourceLoader);
    }

    @Override
    public void afterPropertiesSet() throws PersistenceException {
        String rootPackage;
        PersistenceUnitManager managerToUse = this.persistenceUnitManager;
        if (this.persistenceUnitManager == null) {
            this.internalPersistenceUnitManager.afterPropertiesSet();
            managerToUse = this.internalPersistenceUnitManager;
        }
        this.persistenceUnitInfo = this.determinePersistenceUnitInfo(managerToUse);
        JpaVendorAdapter jpaVendorAdapter = this.getJpaVendorAdapter();
        if (jpaVendorAdapter != null && this.persistenceUnitInfo instanceof SmartPersistenceUnitInfo && (rootPackage = jpaVendorAdapter.getPersistenceProviderRootPackage()) != null) {
            ((SmartPersistenceUnitInfo)this.persistenceUnitInfo).setPersistenceProviderPackageName(rootPackage);
        }
        super.afterPropertiesSet();
    }

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        Assert.state((this.persistenceUnitInfo != null ? 1 : 0) != 0, (String)"PersistenceUnitInfo not initialized");
        PersistenceProvider provider = this.getPersistenceProvider();
        if (provider == null) {
            String providerClassName = this.persistenceUnitInfo.getPersistenceProviderClassName();
            if (providerClassName == null) {
                throw new IllegalArgumentException("No PersistenceProvider specified in EntityManagerFactory configuration, and chosen PersistenceUnitInfo does not specify a provider class name either");
            }
            Class providerClass = ClassUtils.resolveClassName((String)providerClassName, (ClassLoader)this.getBeanClassLoader());
            provider = (PersistenceProvider)BeanUtils.instantiateClass((Class)providerClass);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Building JPA container EntityManagerFactory for persistence unit '" + this.persistenceUnitInfo.getPersistenceUnitName() + "'"));
        }
        EntityManagerFactory emf = provider.createContainerEntityManagerFactory(this.persistenceUnitInfo, this.getJpaPropertyMap());
        this.postProcessEntityManagerFactory(emf, this.persistenceUnitInfo);
        return emf;
    }

    protected PersistenceUnitInfo determinePersistenceUnitInfo(PersistenceUnitManager persistenceUnitManager) {
        if (this.getPersistenceUnitName() != null) {
            return persistenceUnitManager.obtainPersistenceUnitInfo(this.getPersistenceUnitName());
        }
        return persistenceUnitManager.obtainDefaultPersistenceUnitInfo();
    }

    protected void postProcessEntityManagerFactory(EntityManagerFactory emf, PersistenceUnitInfo pui) {
    }

    @Override
    @Nullable
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return this.persistenceUnitInfo;
    }

    @Override
    @Nullable
    public String getPersistenceUnitName() {
        if (this.persistenceUnitInfo != null) {
            return this.persistenceUnitInfo.getPersistenceUnitName();
        }
        return super.getPersistenceUnitName();
    }

    @Override
    public DataSource getDataSource() {
        if (this.persistenceUnitInfo != null) {
            return this.persistenceUnitInfo.getJtaDataSource() != null ? this.persistenceUnitInfo.getJtaDataSource() : this.persistenceUnitInfo.getNonJtaDataSource();
        }
        return this.internalPersistenceUnitManager.getDefaultJtaDataSource() != null ? this.internalPersistenceUnitManager.getDefaultJtaDataSource() : this.internalPersistenceUnitManager.getDefaultDataSource();
    }
}

