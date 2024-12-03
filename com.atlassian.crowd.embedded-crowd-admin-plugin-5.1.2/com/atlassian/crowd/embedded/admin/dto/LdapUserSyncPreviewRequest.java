/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.dto;

import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewRequest;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;

public class LdapUserSyncPreviewRequest
extends UserSyncPreviewRequest {
    private LdapDirectoryConfiguration directoryConfiguration;

    public LdapDirectoryConfiguration getDirectoryConfiguration() {
        return this.directoryConfiguration;
    }

    public void setDirectoryConfiguration(LdapDirectoryConfiguration directoryConfiguration) {
        this.directoryConfiguration = directoryConfiguration;
    }
}

