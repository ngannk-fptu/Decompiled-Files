/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Contained;
import javax.annotation.Nullable;

public final class ContentPermissionExtractorHelper {
    @Nullable
    public static ContentEntityObject getContainerForPermissions(@Nullable Searchable searchable) {
        Object permissionContainer;
        if (searchable instanceof AbstractPage) {
            return (AbstractPage)searchable;
        }
        if (!(searchable instanceof Contained)) {
            return null;
        }
        Object container = ((Contained)searchable).getContainer();
        while (container instanceof Contained && (permissionContainer = ((Contained)container).getContainer()) != null) {
            container = permissionContainer;
        }
        return container;
    }
}

