/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.AbstractPage;
import java.util.Objects;

@Internal
public class ContentExportedToWordEvent {
    private final AbstractPage content;

    public ContentExportedToWordEvent(AbstractPage content) {
        this.content = content;
    }

    public AbstractPage getContent() {
        return this.content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentExportedToWordEvent that = (ContentExportedToWordEvent)o;
        return Objects.equals(this.content, that.content);
    }

    public int hashCode() {
        return Objects.hash(this.content);
    }
}

