/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class LabelListViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = 4708439435526929422L;
    private final String viewType;
    private final Space space;

    public LabelListViewEvent(Object src, Space space, String viewType) {
        super(src);
        this.space = space;
        this.viewType = viewType;
    }

    public Space getSpace() {
        return this.space;
    }

    public String getViewType() {
        return this.viewType;
    }
}

