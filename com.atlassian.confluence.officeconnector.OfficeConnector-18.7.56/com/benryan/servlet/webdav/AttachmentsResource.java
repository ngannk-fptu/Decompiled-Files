/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource
 *  com.atlassian.confluence.pages.AbstractPage
 *  org.apache.jackrabbit.webdav.DavException
 *  org.apache.jackrabbit.webdav.DavResource
 *  org.apache.jackrabbit.webdav.io.InputContext
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.pages.AbstractPage;
import com.benryan.servlet.webdav.AttachmentResource;
import com.benryan.servlet.webdav.ResourceBuilder;
import java.util.Collection;
import java.util.Collections;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.io.InputContext;

public final class AttachmentsResource
extends AbstractCollectionResource {
    public static final String PATH_PREFIX = "attachments";
    private final AbstractPage page;

    public AttachmentsResource(ResourceBuilder builder, AbstractPage page) {
        super(builder.getDavResourceLocator(), builder.getDavResourceFactory(), builder.getLockManager(), builder.getDavSession());
        this.page = page;
    }

    public String getDisplayName() {
        return PATH_PREFIX;
    }

    public boolean exists() {
        return true;
    }

    protected Collection<DavResource> getMemberResources() {
        return Collections.emptySet();
    }

    protected long getCreationtTime() {
        return this.page.getCreationDate().getTime();
    }

    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        AttachmentResource resource = (AttachmentResource)davResource;
        resource.saveData(inputContext);
    }
}

