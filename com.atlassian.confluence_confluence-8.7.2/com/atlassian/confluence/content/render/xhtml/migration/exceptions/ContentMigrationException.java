/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.spaces.Space;

public class ContentMigrationException
extends MigrationException {
    private final long id;
    private final String title;
    private final String spaceKey;
    private final String spaceName;
    private final int version;
    private final String type;

    public ContentMigrationException(ContentEntityObject ceo, Throwable cause) {
        super("Content migration failure", cause);
        ContentEntityObject spacedCeo = ceo;
        if (ceo instanceof Comment) {
            spacedCeo = ((Comment)ceo).getContainer();
        }
        if (spacedCeo instanceof SpaceContentEntityObject) {
            Space space = ((SpaceContentEntityObject)spacedCeo).getSpace();
            if (space != null) {
                this.spaceKey = space.getKey();
                this.spaceName = space.getName();
            } else {
                this.spaceKey = null;
                this.spaceName = null;
            }
        } else {
            this.spaceKey = null;
            this.spaceName = null;
        }
        this.id = ceo.getId();
        this.version = ceo.getVersion();
        this.type = ceo.getType();
        this.title = ceo.getDisplayTitle();
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public int getVersion() {
        return this.version;
    }

    public String getType() {
        return this.type;
    }
}

