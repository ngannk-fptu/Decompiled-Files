/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryMappingNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 */
package com.atlassian.crowd.dao.application;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryMappingNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.InternalApplicationDefaultGroupMembershipConfiguration;
import java.util.List;

public interface ApplicationDefaultGroupMembershipConfigurationDao {
    public void add(Application var1, ApplicationDirectoryMapping var2, String var3) throws DirectoryMappingNotFoundException, ApplicationNotFoundException;

    public void remove(Application var1, ApplicationDirectoryMapping var2, String var3) throws DirectoryMappingNotFoundException, ApplicationNotFoundException;

    public void removeAll(Application var1, ApplicationDirectoryMapping var2) throws DirectoryMappingNotFoundException, ApplicationNotFoundException;

    public List<InternalApplicationDefaultGroupMembershipConfiguration> listAll(Application var1, ApplicationDirectoryMapping var2) throws DirectoryMappingNotFoundException, ApplicationNotFoundException;
}

