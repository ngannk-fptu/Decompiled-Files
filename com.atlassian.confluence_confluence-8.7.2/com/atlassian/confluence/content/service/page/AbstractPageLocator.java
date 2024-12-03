/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.service.AbstractSingleEntityLocator;

public abstract class AbstractPageLocator
extends AbstractSingleEntityLocator
implements PageLocator {
    @Override
    public ConfluenceEntityObject getEntity() {
        return this.getPage();
    }
}

