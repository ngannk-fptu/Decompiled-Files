/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.validator.DirectoryValidationContext;
import com.atlassian.crowd.validator.ValidationError;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public interface CrowdDirectoryService {
    public Directory addDirectory(Directory var1) throws OperationFailedException;

    public List<ValidationError> validateDirectoryConfiguration(Directory var1, EnumSet<DirectoryValidationContext> var2);

    @Nullable
    public Directory findDirectoryByName(String var1);

    public void testConnection(Directory var1) throws OperationFailedException;

    public List<Directory> findAllDirectories();

    public Directory findDirectoryById(long var1);

    public Directory updateDirectory(Directory var1) throws OperationFailedException;

    public void setDirectoryPosition(long var1, int var3) throws OperationFailedException;

    public boolean removeDirectory(long var1) throws DirectoryCurrentlySynchronisingException, OperationFailedException;

    public boolean supportsNestedGroups(long var1) throws OperationFailedException;

    public boolean isDirectorySynchronisable(long var1) throws OperationFailedException;

    public void synchroniseDirectory(long var1) throws OperationFailedException;

    public void synchroniseDirectory(long var1, boolean var3) throws OperationFailedException;

    public boolean isDirectorySynchronising(long var1) throws OperationFailedException;

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long var1) throws OperationFailedException;

    public void setConnectionPoolProperties(ConnectionPoolProperties var1);

    public ConnectionPoolProperties getStoredConnectionPoolProperties();

    public ConnectionPoolProperties getSystemConnectionPoolProperties();

    public boolean isMembershipAggregationEnabled();

    public void setMembershipAggregationEnabled(boolean var1);
}

