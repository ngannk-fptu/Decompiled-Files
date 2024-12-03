/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.content.service.experimental;

import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class PreparedAbstractPage {
    private final long contentId;
    private final Modification<AbstractPage> modification;
    private final SaveContext saveContext;

    PreparedAbstractPage(long contentId, @Nonnull Modification<AbstractPage> modification, SaveContext saveContext) {
        this.contentId = contentId;
        this.modification = Objects.requireNonNull(modification);
        this.saveContext = saveContext;
    }

    public long getContentId() {
        return this.contentId;
    }

    public Modification<AbstractPage> getModification() {
        return this.modification;
    }

    public SaveContext getSaveContext() {
        return this.saveContext;
    }
}

