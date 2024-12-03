/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.template;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class TemplateListViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = 7597498651899633166L;

    public TemplateListViewEvent(Object src, Space space) {
        super(src, space);
    }
}

