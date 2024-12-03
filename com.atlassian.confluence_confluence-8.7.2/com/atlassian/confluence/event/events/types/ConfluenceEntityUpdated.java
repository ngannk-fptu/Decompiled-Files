/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.types;

import com.atlassian.confluence.core.ConfluenceEntityObject;

public interface ConfluenceEntityUpdated {
    public ConfluenceEntityObject getOld();

    public ConfluenceEntityObject getNew();
}

