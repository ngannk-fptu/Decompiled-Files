/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.audit.event;

import com.atlassian.confluence.pages.AbstractPage;
import java.util.Objects;

public class RestrictedPageViewNotPermittedEvent {
    private final AbstractPage page;

    public RestrictedPageViewNotPermittedEvent(AbstractPage page) {
        this.page = Objects.requireNonNull(page);
    }

    public AbstractPage getPage() {
        return this.page;
    }
}

