/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import java.util.Date;

public class RemoteContentSummary {
    private final long id;
    private final String type;
    private final String space;
    private final String status;
    private final String title;
    private final String creator;
    private final Date created;
    private final String modifier;
    private final Date modified;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.core.ContentEntityObject obj \n";

    public RemoteContentSummary(ContentEntityObject obj) {
        this.id = obj.getId();
        this.type = obj.getType();
        this.status = obj.getContentStatus();
        this.title = obj.getDisplayTitle();
        this.creator = obj.getCreatorName();
        this.created = obj.getCreationDate();
        this.modifier = obj.getLastModifierName();
        this.modified = obj.getLastModificationDate();
        if (obj instanceof SpaceContentEntityObject) {
            SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)obj;
            this.space = spaceContentEntityObject.getSpaceKey();
        } else {
            this.space = "";
        }
    }

    public long getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getSpace() {
        return this.space;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCreator() {
        return this.creator;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getModifier() {
        return this.modifier;
    }

    public Date getModified() {
        return this.modified;
    }
}

