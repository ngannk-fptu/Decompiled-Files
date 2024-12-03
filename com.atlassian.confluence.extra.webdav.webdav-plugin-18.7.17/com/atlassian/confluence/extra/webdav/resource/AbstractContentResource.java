/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.IOUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractConfluenceResource;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.struts2.ServletActionContext;

public abstract class AbstractContentResource
extends AbstractConfluenceResource {
    private static final Predicate<Map.Entry<String, String>> NOT_CONTENT_TYPE_HEADERS = new Predicate<Map.Entry<String, String>>(){

        public boolean apply(@Nonnull Map.Entry<String, String> input) {
            return !"Content-Type".equals(input.getKey());
        }
    };

    public AbstractContentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
    }

    protected abstract InputStream getContent();

    protected abstract String getContentType();

    protected abstract long getContentLength();

    protected Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    @Override
    protected SupportedLock getSupportedLock() {
        SupportedLock supportedLock = new SupportedLock();
        supportedLock.addEntry(Type.WRITE, Scope.EXCLUSIVE);
        return supportedLock;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    protected void initProperties(DavPropertySet propertySet) {
        super.initProperties(propertySet);
        propertySet.add(new DefaultDavProperty<Long>(DavPropertyName.GETCONTENTLENGTH, this.getContentLength()));
        propertySet.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTTYPE, this.getContentType()));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void spool(OutputContext outputContext) throws IOException {
        if (outputContext.hasStream()) {
            try (OutputStream outputStream = outputContext.getOutputStream();
                 InputStream inputStream = this.getContent();){
                outputContext.setContentLength(this.getContentLength());
                outputContext.setContentType(this.getContentType());
                outputContext.setModificationTime(this.getModificationTime());
                for (Map.Entry header : Maps.filterEntries(this.getHeaders(), NOT_CONTENT_TYPE_HEADERS).entrySet()) {
                    ServletActionContext.getResponse().setHeader((String)header.getKey(), (String)header.getValue());
                }
                if (inputStream == null || outputStream == null) return;
                IOUtils.copy((InputStream)inputStream, (OutputStream)outputStream);
                return;
            }
        } else {
            outputContext.setContentLength(0L);
            outputContext.setModificationTime(this.getModificationTime());
        }
    }

    @Override
    public final DavResourceIterator getMembers() {
        return new DavResourceIteratorImpl(Collections.EMPTY_LIST);
    }
}

