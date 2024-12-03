/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.event;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.event.events.types.Updated;

public class PluginContentUpdatedEvent
extends ContentEvent
implements Updated,
ConfluenceEntityUpdated {
    private static final long serialVersionUID = 947785665979677079L;
    private final CustomContentEntityObject oldContent;
    private final CustomContentEntityObject currentContent;
    private final SaveContext saveContext;

    public PluginContentUpdatedEvent(Object src, CustomContentEntityObject currentContent, CustomContentEntityObject oldContent, SaveContext saveContext) {
        super(src, false);
        this.oldContent = oldContent;
        this.currentContent = currentContent;
        this.saveContext = saveContext;
    }

    @Override
    public ConfluenceEntityObject getOld() {
        return this.oldContent;
    }

    @Override
    public ConfluenceEntityObject getNew() {
        return this.currentContent;
    }

    @Override
    public ContentEntityObject getContent() {
        return this.currentContent;
    }

    public SaveContext getSaveContext() {
        return this.saveContext;
    }
}

