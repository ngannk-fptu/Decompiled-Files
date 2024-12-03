/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.ConnectionPoolProperties
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.core.CrowdDirectoryServiceImpl
 *  com.atlassian.crowd.embedded.validator.DirectoryValidatorFactory
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.ApplicationManagerException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.validator.DirectoryValidationContext
 *  com.atlassian.crowd.validator.ValidationError
 *  javax.annotation.Nullable
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.core.CrowdDirectoryServiceImpl;
import com.atlassian.crowd.embedded.validator.DirectoryValidatorFactory;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationManagerException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.validator.DirectoryValidationContext;
import com.atlassian.crowd.validator.ValidationError;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public final class ConfluenceCrowdDirectoryService
extends CrowdDirectoryServiceImpl {
    private final ApplicationFactory applicationFactory;
    private final ApplicationManager applicationManager;

    public ConfluenceCrowdDirectoryService(ApplicationFactory applicationFactory, DirectoryInstanceLoader directoryInstanceLoader, DirectoryManager directoryManager, ApplicationManager applicationManager, DirectoryValidatorFactory directoryValidatorFactory) {
        super(applicationFactory, directoryInstanceLoader, directoryManager, applicationManager, directoryValidatorFactory);
        this.applicationFactory = applicationFactory;
        this.applicationManager = applicationManager;
    }

    public void afterPropertiesSet() {
    }

    public Directory addDirectory(Directory directory) throws OperationFailedException {
        Directory addedDirectory = super.addDirectory(directory);
        try {
            this.applicationManager.addDirectoryMapping(this.applicationFactory.getApplication(), addedDirectory, true, OperationType.values());
        }
        catch (ApplicationNotFoundException | DirectoryNotFoundException e) {
            throw new RuntimeException(e);
        }
        return addedDirectory;
    }

    public boolean removeDirectory(long directoryId) throws DirectoryCurrentlySynchronisingException, OperationFailedException {
        Directory directory = super.findDirectoryById(directoryId);
        try {
            this.applicationManager.removeDirectoryFromApplication(directory, this.applicationFactory.getApplication());
        }
        catch (ApplicationManagerException e) {
            throw new RuntimeException(e);
        }
        return super.removeDirectory(directoryId);
    }

    public Directory updateDirectory(Directory directory) throws OperationFailedException {
        Directory updatedDirectory = super.updateDirectory(directory);
        return updatedDirectory;
    }

    public List<ValidationError> validateDirectoryConfiguration(Directory directory, EnumSet<DirectoryValidationContext> validationContexts) {
        return super.validateDirectoryConfiguration(directory, validationContexts);
    }

    @Transactional(readOnly=true)
    @Nullable
    public Directory findDirectoryByName(String name) {
        return super.findDirectoryByName(name);
    }

    @Transactional(readOnly=true)
    public void testConnection(Directory directory) throws OperationFailedException {
        super.testConnection(directory);
    }

    @Transactional(readOnly=true)
    public List<Directory> findAllDirectories() {
        return super.findAllDirectories();
    }

    @Transactional(readOnly=true)
    public Directory findDirectoryById(long directoryId) {
        return super.findDirectoryById(directoryId);
    }

    public void setDirectoryPosition(long directoryId, int position) throws OperationFailedException {
        super.setDirectoryPosition(directoryId, position);
    }

    @Transactional(readOnly=true)
    public boolean supportsNestedGroups(long directoryId) throws OperationFailedException {
        return super.supportsNestedGroups(directoryId);
    }

    @Transactional(readOnly=true)
    public boolean isDirectorySynchronisable(long directoryId) throws OperationFailedException {
        return super.isDirectorySynchronisable(directoryId);
    }

    @Transactional(propagation=Propagation.NEVER)
    public void synchroniseDirectory(long directoryId) throws OperationFailedException {
        super.synchroniseDirectory(directoryId);
    }

    @Transactional(propagation=Propagation.NEVER)
    public void synchroniseDirectory(long directoryId, boolean runInBackground) throws OperationFailedException {
        super.synchroniseDirectory(directoryId, runInBackground);
    }

    @Transactional(readOnly=true)
    public boolean isDirectorySynchronising(long directoryId) throws OperationFailedException {
        return super.isDirectorySynchronising(directoryId);
    }

    @Transactional(readOnly=true)
    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long directoryId) throws OperationFailedException {
        return super.getDirectorySynchronisationInformation(directoryId);
    }

    public void setConnectionPoolProperties(ConnectionPoolProperties poolProperties) {
        super.setConnectionPoolProperties(poolProperties);
    }

    @Transactional(readOnly=true)
    public ConnectionPoolProperties getStoredConnectionPoolProperties() {
        return super.getStoredConnectionPoolProperties();
    }

    @Transactional(readOnly=true)
    public ConnectionPoolProperties getSystemConnectionPoolProperties() {
        return super.getSystemConnectionPoolProperties();
    }

    @Transactional(readOnly=true)
    public boolean isMembershipAggregationEnabled() {
        return super.isMembershipAggregationEnabled();
    }

    public void setMembershipAggregationEnabled(boolean enabled) {
        super.setMembershipAggregationEnabled(enabled);
    }
}

