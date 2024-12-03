/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.event.EntityLinkEvent;

public class EntityLinkDeletedEvent
extends EntityLinkEvent {
    public EntityLinkDeletedEvent(EntityLink entityLink, String localKey, Class<? extends EntityType> localType) {
        super(entityLink, localKey, localType);
    }
}

