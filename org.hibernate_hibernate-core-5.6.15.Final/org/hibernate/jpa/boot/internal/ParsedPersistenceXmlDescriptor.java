/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

public class ParsedPersistenceXmlDescriptor
implements PersistenceUnitDescriptor {
    private final URL persistenceUnitRootUrl;
    private String name;
    private Object nonJtaDataSource;
    private Object jtaDataSource;
    private String providerClassName;
    private PersistenceUnitTransactionType transactionType;
    private boolean useQuotedIdentifiers;
    private boolean excludeUnlistedClasses;
    private ValidationMode validationMode;
    private SharedCacheMode sharedCacheMode;
    private Properties properties = new Properties();
    private List<String> classes = new ArrayList<String>();
    private List<String> mappingFiles = new ArrayList<String>();
    private List<URL> jarFileUrls = new ArrayList<URL>();

    public ParsedPersistenceXmlDescriptor(URL persistenceUnitRootUrl) {
        this.persistenceUnitRootUrl = persistenceUnitRootUrl;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return this.persistenceUnitRootUrl;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }

    public void setNonJtaDataSource(Object nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    @Override
    public Object getJtaDataSource() {
        return this.jtaDataSource;
    }

    public void setJtaDataSource(Object jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }

    @Override
    public String getProviderClassName() {
        return this.providerClassName;
    }

    public void setProviderClassName(String providerClassName) {
        this.providerClassName = providerClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(PersistenceUnitTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public boolean isUseQuotedIdentifiers() {
        return this.useQuotedIdentifiers;
    }

    public void setUseQuotedIdentifiers(boolean useQuotedIdentifiers) {
        this.useQuotedIdentifiers = useQuotedIdentifiers;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public boolean isExcludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }

    public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
        this.excludeUnlistedClasses = excludeUnlistedClasses;
    }

    @Override
    public ValidationMode getValidationMode() {
        return this.validationMode;
    }

    public void setValidationMode(String validationMode) {
        this.validationMode = ValidationMode.valueOf((String)validationMode);
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return this.sharedCacheMode;
    }

    public void setSharedCacheMode(String sharedCacheMode) {
        this.sharedCacheMode = SharedCacheMode.valueOf((String)sharedCacheMode);
    }

    @Override
    public List<String> getManagedClassNames() {
        return this.classes;
    }

    public void addClasses(String ... classes) {
        this.addClasses(Arrays.asList(classes));
    }

    public void addClasses(List<String> classes) {
        this.classes.addAll(classes);
    }

    @Override
    public List<String> getMappingFileNames() {
        return this.mappingFiles;
    }

    public void addMappingFiles(String ... mappingFiles) {
        this.addMappingFiles(Arrays.asList(mappingFiles));
    }

    public void addMappingFiles(List<String> mappingFiles) {
        this.mappingFiles.addAll(mappingFiles);
    }

    @Override
    public List<URL> getJarFileUrls() {
        return this.jarFileUrls;
    }

    public void addJarFileUrl(URL jarFileUrl) {
        this.jarFileUrls.add(jarFileUrl);
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return null;
    }

    @Override
    public void pushClassTransformer(EnhancementContext enhancementContext) {
    }
}

