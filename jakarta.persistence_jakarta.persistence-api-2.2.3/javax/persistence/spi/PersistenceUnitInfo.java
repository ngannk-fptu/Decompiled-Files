/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

public interface PersistenceUnitInfo {
    public String getPersistenceUnitName();

    public String getPersistenceProviderClassName();

    public PersistenceUnitTransactionType getTransactionType();

    public DataSource getJtaDataSource();

    public DataSource getNonJtaDataSource();

    public List<String> getMappingFileNames();

    public List<URL> getJarFileUrls();

    public URL getPersistenceUnitRootUrl();

    public List<String> getManagedClassNames();

    public boolean excludeUnlistedClasses();

    public SharedCacheMode getSharedCacheMode();

    public ValidationMode getValidationMode();

    public Properties getProperties();

    public String getPersistenceXMLSchemaVersion();

    public ClassLoader getClassLoader();

    public void addTransformer(ClassTransformer var1);

    public ClassLoader getNewTempClassLoader();
}

