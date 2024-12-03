/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.ClassTransformer
 *  javax.persistence.spi.PersistenceUnitTransactionType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.orm.jpa.persistenceunit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class MutablePersistenceUnitInfo
implements SmartPersistenceUnitInfo {
    @Nullable
    private String persistenceUnitName;
    @Nullable
    private String persistenceProviderClassName;
    @Nullable
    private PersistenceUnitTransactionType transactionType;
    @Nullable
    private DataSource nonJtaDataSource;
    @Nullable
    private DataSource jtaDataSource;
    private final List<String> mappingFileNames = new ArrayList<String>();
    private final List<URL> jarFileUrls = new ArrayList<URL>();
    @Nullable
    private URL persistenceUnitRootUrl;
    private final List<String> managedClassNames = new ArrayList<String>();
    private final List<String> managedPackages = new ArrayList<String>();
    private boolean excludeUnlistedClasses = false;
    private SharedCacheMode sharedCacheMode = SharedCacheMode.UNSPECIFIED;
    private ValidationMode validationMode = ValidationMode.AUTO;
    private Properties properties = new Properties();
    private String persistenceXMLSchemaVersion = "2.0";
    @Nullable
    private String persistenceProviderPackageName;

    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Nullable
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    public void setPersistenceProviderClassName(@Nullable String persistenceProviderClassName) {
        this.persistenceProviderClassName = persistenceProviderClassName;
    }

    @Nullable
    public String getPersistenceProviderClassName() {
        return this.persistenceProviderClassName;
    }

    public void setTransactionType(PersistenceUnitTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PersistenceUnitTransactionType getTransactionType() {
        if (this.transactionType != null) {
            return this.transactionType;
        }
        return this.jtaDataSource != null ? PersistenceUnitTransactionType.JTA : PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    public void setJtaDataSource(@Nullable DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }

    @Nullable
    public DataSource getJtaDataSource() {
        return this.jtaDataSource;
    }

    public void setNonJtaDataSource(@Nullable DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    @Nullable
    public DataSource getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }

    public void addMappingFileName(String mappingFileName) {
        this.mappingFileNames.add(mappingFileName);
    }

    public List<String> getMappingFileNames() {
        return this.mappingFileNames;
    }

    public void addJarFileUrl(URL jarFileUrl) {
        this.jarFileUrls.add(jarFileUrl);
    }

    public List<URL> getJarFileUrls() {
        return this.jarFileUrls;
    }

    public void setPersistenceUnitRootUrl(@Nullable URL persistenceUnitRootUrl) {
        this.persistenceUnitRootUrl = persistenceUnitRootUrl;
    }

    @Nullable
    public URL getPersistenceUnitRootUrl() {
        return this.persistenceUnitRootUrl;
    }

    public void addManagedClassName(String managedClassName) {
        this.managedClassNames.add(managedClassName);
    }

    public List<String> getManagedClassNames() {
        return this.managedClassNames;
    }

    public void addManagedPackage(String packageName) {
        this.managedPackages.add(packageName);
    }

    @Override
    public List<String> getManagedPackages() {
        return this.managedPackages;
    }

    public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
        this.excludeUnlistedClasses = excludeUnlistedClasses;
    }

    public boolean excludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
    }

    public SharedCacheMode getSharedCacheMode() {
        return this.sharedCacheMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    public ValidationMode getValidationMode() {
        return this.validationMode;
    }

    public void addProperty(String name, String value) {
        this.properties.setProperty(name, value);
    }

    public void setProperties(Properties properties) {
        Assert.notNull((Object)properties, (String)"Properties must not be null");
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setPersistenceXMLSchemaVersion(String persistenceXMLSchemaVersion) {
        this.persistenceXMLSchemaVersion = persistenceXMLSchemaVersion;
    }

    public String getPersistenceXMLSchemaVersion() {
        return this.persistenceXMLSchemaVersion;
    }

    @Override
    public void setPersistenceProviderPackageName(@Nullable String persistenceProviderPackageName) {
        this.persistenceProviderPackageName = persistenceProviderPackageName;
    }

    @Nullable
    public String getPersistenceProviderPackageName() {
        return this.persistenceProviderPackageName;
    }

    @Nullable
    public ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    public void addTransformer(ClassTransformer classTransformer) {
        throw new UnsupportedOperationException("addTransformer not supported");
    }

    public ClassLoader getNewTempClassLoader() {
        throw new UnsupportedOperationException("getNewTempClassLoader not supported");
    }

    public String toString() {
        return "PersistenceUnitInfo: name '" + this.persistenceUnitName + "', root URL [" + this.persistenceUnitRootUrl + "]";
    }
}

