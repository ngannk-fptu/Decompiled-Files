/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;

public class CustomContentBreadcrumb
extends AbstractBreadcrumb {
    private final CustomContentEntityObject customContentEntity;

    public CustomContentBreadcrumb(CustomContentEntityObject customContentEntity) {
        super(customContentEntity.getTitle(), customContentEntity.getUrlPath());
        this.customContentEntity = customContentEntity;
    }

    @Override
    protected Breadcrumb getParent() {
        if (this.customContentEntity.getSpace() != null) {
            return new SpaceBreadcrumb(this.customContentEntity.getSpace());
        }
        return null;
    }
}

