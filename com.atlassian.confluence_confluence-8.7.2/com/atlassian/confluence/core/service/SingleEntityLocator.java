/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.service.EntityLocator;

public interface SingleEntityLocator
extends EntityLocator {
    public ConfluenceEntityObject getEntity();
}

