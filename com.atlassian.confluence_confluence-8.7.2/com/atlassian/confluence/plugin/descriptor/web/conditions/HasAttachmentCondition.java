/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.spring.container.LazyComponentReference;
import com.google.common.base.Supplier;
import java.util.Optional;

public class HasAttachmentCondition
extends BaseConfluenceCondition {
    private AttachmentManager attachmentManager;
    private final Supplier<AttachmentManager> attachmentManagerReference = new LazyComponentReference("attachmentManager");

    @Override
    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        AbstractPage page = webInterfaceContext.getPage();
        return page != null && this.getAttachmentManager().countLatestVersionsOfAttachments(page) > 0;
    }

    private AttachmentManager getAttachmentManager() {
        return Optional.ofNullable(this.attachmentManager).orElseGet(() -> this.attachmentManagerReference.get());
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

