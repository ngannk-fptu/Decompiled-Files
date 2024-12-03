/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.service.SingleEntityLocator;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSingleEntityLocator
implements SingleEntityLocator {
    @Override
    public List<ConfluenceEntityObject> getEntities() {
        ConfluenceEntityObject entity = this.getEntity();
        if (entity == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.singletonList(entity);
    }
}

