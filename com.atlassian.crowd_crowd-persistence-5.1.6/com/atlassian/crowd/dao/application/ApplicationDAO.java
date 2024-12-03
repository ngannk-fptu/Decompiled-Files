/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.crowd.dao.application;

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

public interface ApplicationDAO {
    public Application findById(long var1) throws ApplicationNotFoundException;

    public Application findByName(String var1) throws ApplicationNotFoundException;

    public Application add(Application var1, PasswordCredential var2);

    public Application update(Application var1) throws ApplicationNotFoundException;

    public void updateCredential(Application var1, PasswordCredential var2) throws ApplicationNotFoundException;

    public void remove(Application var1);

    public List<Application> search(EntityQuery<Application> var1);

    public void addDirectoryMapping(long var1, long var3, boolean var5, OperationType ... var6) throws DirectoryNotFoundException, ApplicationNotFoundException;

    public void addRemoteAddress(long var1, RemoteAddress var3) throws ApplicationNotFoundException;

    public void removeRemoteAddress(long var1, RemoteAddress var3) throws ApplicationNotFoundException;

    public void removeDirectoryMapping(long var1, long var3) throws ApplicationNotFoundException;

    public void removeDirectoryMappings(long var1);

    public void addGroupMapping(long var1, long var3, String var5) throws ApplicationNotFoundException;

    public void removeGroupMapping(long var1, long var3, String var5) throws ApplicationNotFoundException;

    public void removeGroupMappings(long var1, String var3);

    public void updateDirectoryMapping(long var1, long var3, int var5) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public List<Application> findAuthorisedApplications(long var1, List<String> var3);

    public void updateDirectoryMapping(long var1, long var3, boolean var5) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public void updateDirectoryMapping(long var1, long var3, boolean var5, Set<OperationType> var6) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public DirectoryMapping findDirectoryMapping(long var1, long var3) throws ApplicationNotFoundException, DirectoryMappingNotFoundException;
}

