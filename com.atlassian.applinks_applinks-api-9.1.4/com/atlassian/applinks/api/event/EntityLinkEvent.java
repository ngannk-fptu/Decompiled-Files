/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.event.LinkEvent;

public abstract class EntityLinkEvent
implements LinkEvent {
    protected final EntityLink entityLink;
    protected final String localKey;
    protected final Class<? extends EntityType> localType;

    protected EntityLinkEvent(EntityLink entityLink, String localKey, Class<? extends EntityType> localType) {
        this.entityLink = entityLink;
        this.localKey = localKey;
        this.localType = localType;
    }

    public ApplicationId getApplicationId() {
        return this.entityLink.getApplicationLink().getId();
    }

    public ApplicationType getApplicationType() {
        return this.entityLink.getApplicationLink().getType();
    }

    public EntityType getEntityType() {
        return this.entityLink.getType();
    }

    public String getEntityKey() {
        return this.entityLink.getKey();
    }

    public String getLocalKey() {
        return this.localKey;
    }

    public Class<? extends EntityType> getLocalType() {
        return this.localType;
    }
}

