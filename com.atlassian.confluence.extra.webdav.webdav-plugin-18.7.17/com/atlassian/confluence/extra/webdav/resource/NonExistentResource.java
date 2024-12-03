/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractConfluenceResource;
import java.io.IOException;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SupportedLock;

public class NonExistentResource
extends AbstractConfluenceResource {
    public NonExistentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
    }

    @Override
    protected long getCreationtTime() {
        return 0L;
    }

    @Override
    protected SupportedLock getSupportedLock() {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getDisplayName() {
        String[] resourcePathTokens = StringUtils.split((String)this.getResourcePath(), (char)'/');
        return resourcePathTokens.length >= 1 ? resourcePathTokens[resourcePathTokens.length - 1] : "/";
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        outputContext.setContentLength(0L);
        outputContext.setModificationTime(this.getModificationTime());
    }

    @Override
    public DavResourceIterator getMembers() {
        return new DavResourceIteratorImpl(Collections.EMPTY_LIST);
    }
}

