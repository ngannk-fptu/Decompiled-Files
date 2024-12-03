/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource
 *  com.atlassian.confluence.pages.AbstractPage
 *  org.apache.jackrabbit.webdav.DavResource
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.pages.AbstractPage;
import com.benryan.servlet.webdav.ResourceBuilder;
import java.util.Collection;
import java.util.Collections;
import org.apache.jackrabbit.webdav.DavResource;

public class PageResource
extends AbstractCollectionResource {
    private final AbstractPage page;

    public PageResource(ResourceBuilder builder, AbstractPage page) {
        super(builder.getDavResourceLocator(), builder.getDavResourceFactory(), builder.getLockManager(), builder.getDavSession());
        this.page = page;
    }

    public String getDisplayName() {
        return this.page.getIdAsString();
    }

    public boolean exists() {
        return this.page != null;
    }

    protected Collection<DavResource> getMemberResources() {
        return Collections.emptySet();
    }

    protected long getCreationtTime() {
        return this.page.getCreationDate().getTime();
    }
}

