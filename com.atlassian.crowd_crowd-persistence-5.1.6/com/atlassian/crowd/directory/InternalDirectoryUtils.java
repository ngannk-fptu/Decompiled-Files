/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import java.util.Set;

public interface InternalDirectoryUtils {
    public void validateDirectoryForEntity(DirectoryEntity var1, Long var2);

    public void validateUsername(String var1);

    public void validateCredential(User var1, PasswordCredential var2, Set<PasswordConstraint> var3, String var4) throws InvalidCredentialException;

    public void validateGroupName(Group var1, String var2);
}

