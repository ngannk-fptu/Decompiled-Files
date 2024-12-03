/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceGroupJoiningAuthenticator;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator;
import com.atlassian.spring.container.ContainerManager;

@AuthenticationContextAwareAuthenticator
public class ConfluenceLDAPGroupJoiningAuthenticator
extends ConfluenceGroupJoiningAuthenticator {
    @Override
    void postLogin(User user) {
        Directory directory = ConfluenceLDAPGroupJoiningAuthenticator.getCrowdDirectoryService().findDirectoryById(user.getDirectoryId());
        if (directory != null && this.isLdapDirectory(directory)) {
            super.postLogin(user);
        }
    }

    private boolean isLdapDirectory(Directory directory) {
        DirectoryType type = directory.getType();
        return type == DirectoryType.CONNECTOR || type == DirectoryType.DELEGATING;
    }

    private static CrowdDirectoryService getCrowdDirectoryService() {
        return (CrowdDirectoryService)ContainerManager.getComponent((String)"crowdDirectoryService");
    }
}

