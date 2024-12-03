/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.dto;

import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewRequest;

public class CrowdUserSyncPreviewRequest
extends UserSyncPreviewRequest {
    private CrowdDirectoryConfiguration directoryConfiguration;

    public CrowdDirectoryConfiguration getDirectoryConfiguration() {
        return this.directoryConfiguration;
    }

    public void setDirectoryConfiguration(CrowdDirectoryConfiguration directoryConfiguration) {
        this.directoryConfiguration = directoryConfiguration;
    }
}

