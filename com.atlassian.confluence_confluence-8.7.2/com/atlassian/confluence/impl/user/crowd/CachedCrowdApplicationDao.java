/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryMappingNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.impl.user.crowd.ApplicationCache;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryMappingNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.Set;

public final class CachedCrowdApplicationDao
implements ApplicationDAO {
    private ApplicationDAO dao;
    private ApplicationCache cache;

    public void setDelegate(ApplicationDAO delegate) {
        this.dao = delegate;
    }

    public void setCache(ApplicationCache cache) {
        this.cache = cache;
    }

    public Application findById(long id) throws ApplicationNotFoundException {
        return this.dao.findById(id);
    }

    public Application findByName(String name) throws ApplicationNotFoundException {
        return this.cache.getApplication(name, arg_0 -> ((ApplicationDAO)this.dao).findByName(arg_0));
    }

    public Application add(Application application, PasswordCredential passwordCredential) {
        Application savedApplication = this.dao.add(application, passwordCredential);
        this.cache.removeApplication(savedApplication);
        return savedApplication;
    }

    public Application update(Application application) throws ApplicationNotFoundException {
        Application savedApplication = this.dao.update(application);
        this.cache.removeApplication(savedApplication);
        return savedApplication;
    }

    public void updateCredential(Application application, PasswordCredential passwordCredential) throws ApplicationNotFoundException {
        this.dao.updateCredential(application, passwordCredential);
        this.cache.removeApplication(application);
    }

    public void remove(Application application) {
        this.dao.remove(application);
        this.cache.removeApplication(application.getName());
    }

    public List<Application> search(EntityQuery<Application> query) {
        return this.dao.search(query);
    }

    public void addDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate, OperationType ... operationTypes) throws DirectoryNotFoundException, ApplicationNotFoundException {
        this.dao.addDirectoryMapping(applicationId, directoryId, allowAllToAuthenticate, operationTypes);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void addRemoteAddress(long applicationId, RemoteAddress remoteAddress) throws ApplicationNotFoundException {
        this.dao.addRemoteAddress(applicationId, remoteAddress);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void removeRemoteAddress(long applicationId, RemoteAddress remoteAddress) throws ApplicationNotFoundException {
        this.dao.removeRemoteAddress(applicationId, remoteAddress);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void removeDirectoryMapping(long applicationId, long directoryId) throws ApplicationNotFoundException {
        this.dao.removeDirectoryMapping(applicationId, directoryId);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void removeDirectoryMappings(long directoryId) {
        this.dao.removeDirectoryMappings(directoryId);
        this.cache.removeAll();
    }

    public void addGroupMapping(long applicationId, long directoryId, String groupName) throws ApplicationNotFoundException {
        this.dao.addGroupMapping(applicationId, directoryId, groupName);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void removeGroupMapping(long applicationId, long directoryId, String groupName) throws ApplicationNotFoundException {
        this.dao.removeGroupMapping(applicationId, directoryId, groupName);
        try {
            this.cache.removeApplication(this.dao.findById(applicationId));
        }
        catch (ApplicationNotFoundException applicationNotFoundException) {
            // empty catch block
        }
    }

    public void removeGroupMappings(long directoryId, String groupName) {
        this.dao.removeGroupMappings(directoryId, groupName);
        this.cache.removeAll();
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, int position) throws ApplicationNotFoundException, DirectoryNotFoundException {
        this.dao.updateDirectoryMapping(applicationId, directoryId, position);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public List<Application> findAuthorisedApplications(long directoryId, List<String> groupNames) {
        return this.dao.findAuthorisedApplications(directoryId, groupNames);
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate) throws ApplicationNotFoundException, DirectoryNotFoundException {
        this.dao.updateDirectoryMapping(applicationId, directoryId, allowAllToAuthenticate);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public void updateDirectoryMapping(long applicationId, long directoryId, boolean allowAllToAuthenticate, Set<OperationType> operationTypes) throws ApplicationNotFoundException, DirectoryNotFoundException {
        this.dao.updateDirectoryMapping(applicationId, directoryId, allowAllToAuthenticate, operationTypes);
        this.cache.removeApplication(this.dao.findById(applicationId));
    }

    public DirectoryMapping findDirectoryMapping(long applicationId, long directoryId) throws ApplicationNotFoundException, DirectoryMappingNotFoundException {
        return this.dao.findDirectoryMapping(applicationId, directoryId);
    }
}

