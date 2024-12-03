/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.ConnectionPoolProperties
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties
 *  com.atlassian.crowd.embedded.impl.SystemConnectionPoolProperties
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.CommunicationException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.ApplicationManagerException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.validator.DirectoryValidationContext
 *  com.atlassian.crowd.validator.ValidationError
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.http.conn.ConnectTimeoutException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.core;

import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.core.util.EnvironmentUtils;
import com.atlassian.crowd.embedded.core.util.JndiLdapConnectionPoolUtils;
import com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties;
import com.atlassian.crowd.embedded.impl.SystemConnectionPoolProperties;
import com.atlassian.crowd.embedded.validator.DirectoryValidatorFactory;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.runtime.CommunicationException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationManagerException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.validator.DirectoryValidationContext;
import com.atlassian.crowd.validator.ValidationError;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdDirectoryServiceImpl
implements CrowdDirectoryService {
    private final Logger log = LoggerFactory.getLogger(CrowdDirectoryServiceImpl.class);
    private final DirectoryManager directoryManager;
    private final ApplicationManager applicationManager;
    private final ApplicationFactory applicationFactory;
    private final DirectoryInstanceLoader directoryInstanceLoader;
    private volatile boolean ldapConnectionPoolSettingsApplied;
    private final DirectoryValidatorFactory directoryValidatorFactory;

    public CrowdDirectoryServiceImpl(ApplicationFactory applicationFactory, DirectoryInstanceLoader directoryInstanceLoader, DirectoryManager directoryManager, ApplicationManager applicationManager, DirectoryValidatorFactory directoryValidatorFactory) {
        this.directoryManager = (DirectoryManager)Preconditions.checkNotNull((Object)directoryManager);
        this.applicationManager = (ApplicationManager)Preconditions.checkNotNull((Object)applicationManager);
        this.applicationFactory = (ApplicationFactory)Preconditions.checkNotNull((Object)applicationFactory);
        this.directoryInstanceLoader = (DirectoryInstanceLoader)Preconditions.checkNotNull((Object)directoryInstanceLoader);
        this.directoryValidatorFactory = directoryValidatorFactory;
        this.afterPropertiesSet();
    }

    public void afterPropertiesSet() {
        if (this.applicationFactory.isEmbeddedCrowd()) {
            Optional.ofNullable(this.applicationFactory.getApplication()).ifPresent(this::applyJndiConnectionPoolSystemProperties);
        } else {
            this.ldapConnectionPoolSettingsApplied = true;
        }
    }

    public Directory addDirectory(Directory directory) throws OperationFailedException {
        try {
            return this.directoryManager.addDirectory(directory);
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public List<ValidationError> validateDirectoryConfiguration(Directory directory, EnumSet<DirectoryValidationContext> validationContexts) {
        return this.directoryValidatorFactory.getValidator(directory.getType(), validationContexts).validate(directory);
    }

    @Nullable
    public Directory findDirectoryByName(String name) {
        try {
            return this.directoryManager.findDirectoryByName(name);
        }
        catch (DirectoryNotFoundException e) {
            return null;
        }
    }

    public void testConnection(Directory directory) throws OperationFailedException {
        try {
            this.directoryInstanceLoader.getRawDirectory(null, directory.getImplementationClass(), directory.getAttributes()).testConnection();
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            Optional<Throwable> throwable = Throwables.getCausalChain((Throwable)e).stream().filter(ex -> ex instanceof UnknownHostException || ex instanceof ConnectTimeoutException).findFirst();
            if (throwable.isPresent()) {
                throw new CommunicationException(throwable.get().getMessage(), throwable.get());
            }
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public List<Directory> findAllDirectories() {
        Application application = this.getApplication();
        if (application == null) {
            return Collections.emptyList();
        }
        return Lists.transform((List)application.getDirectoryMappings(), (Function)new Function<DirectoryMapping, Directory>(){

            public Directory apply(DirectoryMapping from) {
                return from.getDirectory();
            }
        });
    }

    public Directory findDirectoryById(long directoryId) {
        try {
            return this.directoryManager.findDirectoryById(directoryId);
        }
        catch (DirectoryNotFoundException e) {
            return null;
        }
    }

    public Directory updateDirectory(Directory directory) throws OperationFailedException {
        try {
            return this.directoryManager.updateDirectory(directory);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void setDirectoryPosition(long directoryId, int position) throws OperationFailedException {
        try {
            this.applicationManager.updateDirectoryMapping(this.getApplication(), this.findDirectoryById(directoryId), position);
        }
        catch (ApplicationNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public boolean removeDirectory(long directoryId) throws DirectoryCurrentlySynchronisingException, OperationFailedException {
        Directory directory = this.findDirectoryById(directoryId);
        if (directory != null) {
            try {
                this.directoryManager.removeDirectory(directory);
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        return this.findDirectoryById(directoryId) != null;
    }

    public boolean supportsNestedGroups(long directoryId) throws OperationFailedException {
        try {
            return this.directoryManager.supportsNestedGroups(directoryId);
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public boolean isDirectorySynchronisable(long directoryId) throws OperationFailedException {
        try {
            return this.directoryManager.isSynchronisable(directoryId);
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void synchroniseDirectory(long directoryId) throws OperationFailedException {
        this.synchroniseDirectory(directoryId, true);
    }

    public void synchroniseDirectory(long directoryId, boolean runInBackground) throws OperationFailedException {
        try {
            SynchronisationMode synchronisationMode = this.directoryManager.getSynchronisationMode(directoryId);
            this.directoryManager.synchroniseCache(directoryId, synchronisationMode, runInBackground);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean isDirectorySynchronising(long directoryId) throws OperationFailedException {
        try {
            return this.directoryManager.isSynchronising(directoryId);
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long directoryId) throws OperationFailedException {
        try {
            return this.directoryManager.getDirectorySynchronisationInformation(directoryId);
        }
        catch (DirectoryInstantiationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void setConnectionPoolProperties(ConnectionPoolProperties poolProperties) {
        ApplicationImpl template = ApplicationImpl.newInstance((Application)this.getApplication());
        template.getAttributes().putAll(poolProperties.toPropertiesMap());
        try {
            this.applicationManager.update((Application)template);
        }
        catch (ApplicationManagerException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (ApplicationNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public ConnectionPoolProperties getStoredConnectionPoolProperties() {
        return Optional.ofNullable(this.getApplication()).map(this::getPersistedConnectionPoolProperties).orElse((ConnectionPoolProperties)new DefaultConnectionPoolProperties());
    }

    private Application getApplication() {
        Application application = this.applicationFactory.getApplication();
        if (application == null) {
            return null;
        }
        this.applyJndiConnectionPoolSystemProperties(application);
        return application;
    }

    public ConnectionPoolProperties getSystemConnectionPoolProperties() {
        return SystemConnectionPoolProperties.getInstance();
    }

    public boolean isMembershipAggregationEnabled() {
        return this.getApplication().isMembershipAggregationEnabled();
    }

    public void setMembershipAggregationEnabled(boolean enabled) {
        ApplicationImpl template = ApplicationImpl.newInstance((Application)this.getApplication());
        template.setMembershipAggregationEnabled(enabled);
        try {
            this.applicationManager.update((Application)template);
        }
        catch (ApplicationManagerException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (ApplicationNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private ConnectionPoolProperties getPersistedConnectionPoolProperties(Application application) {
        return DefaultConnectionPoolProperties.fromPropertiesMap((Map)application.getAttributes());
    }

    private void applyJndiConnectionPoolSystemProperties(Application application) {
        if (!this.ldapConnectionPoolSettingsApplied) {
            if (this.isUsingJre8()) {
                if (JndiLdapConnectionPoolUtils.isPoolTimeoutUnlimited()) {
                    this.log.warn("JNDI Pool timeout has value <0> (unlimited). This is not recommended as it might cause issues");
                }
            } else {
                JndiLdapConnectionPoolUtils.setPersistedJndiLdapPoolSystemProperties(this.getPersistedConnectionPoolProperties(application));
                JndiLdapConnectionPoolUtils.initJndiLdapPools();
            }
            this.ldapConnectionPoolSettingsApplied = true;
        }
    }

    private boolean isUsingJre8() {
        return EnvironmentUtils.getJreVersion().filter(EnvironmentUtils.JRE.JRE_8::equals).isPresent();
    }
}

