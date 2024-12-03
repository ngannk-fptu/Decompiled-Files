/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.ClassTransformer
 *  javax.persistence.spi.PersistenceUnitInfo
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.internal;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.internal.enhance.EnhancingClassTransformerImpl;

public class PersistenceUnitInfoDescriptor
implements PersistenceUnitDescriptor {
    private final PersistenceUnitInfo persistenceUnitInfo;

    public PersistenceUnitInfoDescriptor(PersistenceUnitInfo persistenceUnitInfo) {
        this.persistenceUnitInfo = persistenceUnitInfo;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return this.persistenceUnitInfo.getPersistenceUnitRootUrl();
    }

    @Override
    public String getName() {
        return this.persistenceUnitInfo.getPersistenceUnitName();
    }

    @Override
    public Object getNonJtaDataSource() {
        return this.persistenceUnitInfo.getNonJtaDataSource();
    }

    @Override
    public Object getJtaDataSource() {
        return this.persistenceUnitInfo.getJtaDataSource();
    }

    @Override
    public String getProviderClassName() {
        return this.persistenceUnitInfo.getPersistenceProviderClassName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return this.persistenceUnitInfo.getTransactionType();
    }

    @Override
    public boolean isUseQuotedIdentifiers() {
        return false;
    }

    @Override
    public Properties getProperties() {
        return this.persistenceUnitInfo.getProperties();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.persistenceUnitInfo.getClassLoader();
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return this.persistenceUnitInfo.getNewTempClassLoader();
    }

    @Override
    public boolean isExcludeUnlistedClasses() {
        return this.persistenceUnitInfo.excludeUnlistedClasses();
    }

    @Override
    public ValidationMode getValidationMode() {
        return this.persistenceUnitInfo.getValidationMode();
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return this.persistenceUnitInfo.getSharedCacheMode();
    }

    @Override
    public List<String> getManagedClassNames() {
        return this.persistenceUnitInfo.getManagedClassNames();
    }

    @Override
    public List<String> getMappingFileNames() {
        return this.persistenceUnitInfo.getMappingFileNames();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return this.persistenceUnitInfo.getJarFileUrls();
    }

    @Override
    public void pushClassTransformer(EnhancementContext enhancementContext) {
        this.persistenceUnitInfo.addTransformer((ClassTransformer)new EnhancingClassTransformerImpl(enhancementContext));
    }
}

