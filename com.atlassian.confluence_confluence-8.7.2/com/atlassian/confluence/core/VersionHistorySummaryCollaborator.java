/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;

public class VersionHistorySummaryCollaborator {
    private final long id;
    private final int version;
    private final Date lastModificationDate;
    private final String versionComment;
    private final ConfluenceUser lastModifier;
    private final ConfluenceUser collaborator;

    public VersionHistorySummaryCollaborator(long id, int version, Date lastModificationDate, String versionComment, ConfluenceUser lastModifier, ConfluenceUser collaborator) {
        this.id = id;
        this.version = version;
        this.lastModificationDate = lastModificationDate;
        this.versionComment = versionComment;
        this.lastModifier = lastModifier;
        this.collaborator = collaborator;
    }

    public long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public String getVersionComment() {
        return this.versionComment;
    }

    public ConfluenceUser getLastModifier() {
        return this.lastModifier;
    }

    public ConfluenceUser getCollaborator() {
        return this.collaborator;
    }
}

