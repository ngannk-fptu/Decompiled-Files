/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.ApplicationAlreadyExistsException
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.RemoteAddress
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.ApplicationAlreadyExistsException;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.manager.application.ApplicationManagerException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.Set;

public interface ApplicationManager {
    public Application add(Application var1) throws InvalidCredentialException, ApplicationAlreadyExistsException;

    public Application findById(long var1) throws ApplicationNotFoundException;

    public Application findByName(String var1) throws ApplicationNotFoundException;

    public void remove(Application var1) throws ApplicationManagerException;

    public void removeDirectoryFromApplication(Directory var1, Application var2) throws ApplicationManagerException;

    public void addDirectoryMapping(Application var1, Directory var2, boolean var3, OperationType ... var4) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public void updateDirectoryMapping(Application var1, Directory var2, int var3) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public void updateDirectoryMapping(Application var1, Directory var2, boolean var3) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public void updateDirectoryMapping(Application var1, Directory var2, boolean var3, Set<OperationType> var4) throws ApplicationNotFoundException, DirectoryNotFoundException;

    public void addRemoteAddress(Application var1, RemoteAddress var2) throws ApplicationNotFoundException;

    public void removeRemoteAddress(Application var1, RemoteAddress var2) throws ApplicationNotFoundException;

    public void addGroupMapping(Application var1, Directory var2, String var3) throws ApplicationNotFoundException;

    public void removeGroupMapping(Application var1, Directory var2, String var3) throws ApplicationNotFoundException;

    public Application update(Application var1) throws ApplicationManagerException, ApplicationNotFoundException;

    public void updateCredential(Application var1, PasswordCredential var2) throws ApplicationManagerException, ApplicationNotFoundException;

    public boolean authenticate(Application var1, PasswordCredential var2) throws ApplicationNotFoundException;

    public List<Application> search(EntityQuery var1);

    public List<Application> findAll();
}

