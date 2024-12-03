/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.PropertySet;
import java.net.URI;

public interface EntityLink
extends PropertySet {
    public ApplicationLink getApplicationLink();

    public EntityType getType();

    public String getKey();

    public String getName();

    public URI getDisplayUrl();

    public boolean isPrimary();
}

