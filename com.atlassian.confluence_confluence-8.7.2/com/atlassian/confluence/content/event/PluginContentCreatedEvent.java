/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.event;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.Created;

public class PluginContentCreatedEvent
extends ContentEvent
implements Created {
    private static final long serialVersionUID = 8797773230722715355L;
    private final CustomContentEntityObject content;

    @Deprecated
    public PluginContentCreatedEvent(Object src, CustomContentEntityObject content) {
        super(src, false);
        this.content = content;
    }

    public PluginContentCreatedEvent(Object source, CustomContentEntityObject content, OperationContext<?> context) {
        super(source, context);
        this.content = content;
    }

    @Override
    public CustomContentEntityObject getContent() {
        return this.content;
    }
}

