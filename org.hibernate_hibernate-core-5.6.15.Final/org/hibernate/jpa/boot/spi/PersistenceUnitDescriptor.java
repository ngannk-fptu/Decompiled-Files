/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.spi;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;

public interface PersistenceUnitDescriptor {
    public URL getPersistenceUnitRootUrl();

    public String getName();

    public String getProviderClassName();

    public boolean isUseQuotedIdentifiers();

    public boolean isExcludeUnlistedClasses();

    public PersistenceUnitTransactionType getTransactionType();

    public ValidationMode getValidationMode();

    public SharedCacheMode getSharedCacheMode();

    public List<String> getManagedClassNames();

    public List<String> getMappingFileNames();

    public List<URL> getJarFileUrls();

    public Object getNonJtaDataSource();

    public Object getJtaDataSource();

    public Properties getProperties();

    public ClassLoader getClassLoader();

    public ClassLoader getTempClassLoader();

    public void pushClassTransformer(EnhancementContext var1);
}

