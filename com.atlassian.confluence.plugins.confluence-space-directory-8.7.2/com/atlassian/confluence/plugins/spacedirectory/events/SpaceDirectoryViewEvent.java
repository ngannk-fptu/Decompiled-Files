/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.types.Viewed
 */
package com.atlassian.confluence.plugins.spacedirectory.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.plugins.spacedirectory.ViewSpaceDirectoryAction;

@EventName(value="confluence.space-directory.view")
public class SpaceDirectoryViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = -8103602766971767218L;

    public SpaceDirectoryViewEvent(ViewSpaceDirectoryAction spaceDirectoryAction) {
        super((Object)spaceDirectoryAction);
    }
}

