/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.event;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.Removed;

public class PluginContentRemovedEvent
extends ContentEvent
implements Removed {
    private static final long serialVersionUID = 7257138419942363874L;
    private final CustomContentEntityObject content;

    public PluginContentRemovedEvent(Object src, CustomContentEntityObject content) {
        super(src, false);
        this.content = content;
    }

    @Override
    public CustomContentEntityObject getContent() {
        return this.content;
    }
}

