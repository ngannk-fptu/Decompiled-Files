/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.UserWithAttributes
 */
package com.atlassian.crowd.core.event.listener;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.UserWithAttributes;
import java.util.Collection;

public interface DefaultGroupMembershipResolver {
    public Collection<String> getDefaultGroupNames(Application var1, Directory var2, UserWithAttributes var3);

    public void onDefaultGroupsAdded(Application var1, Directory var2, UserWithAttributes var3) throws OperationFailedException;
}

