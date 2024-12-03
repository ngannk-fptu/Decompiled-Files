/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.people;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.user.actions.PeopleDirectoryAction;

@EventName(value="confluence.people-directory.view")
public class PeopleDirectoryViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = 741021270785104196L;

    public PeopleDirectoryViewEvent(PeopleDirectoryAction peopleDirectory) {
        super(peopleDirectory);
    }
}

