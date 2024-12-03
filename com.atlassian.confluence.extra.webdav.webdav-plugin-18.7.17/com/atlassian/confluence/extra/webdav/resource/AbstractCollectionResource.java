/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.PlainTextToHtmlConverter
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractConfluenceResource;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SupportedLock;

public abstract class AbstractCollectionResource
extends AbstractConfluenceResource {
    protected static final String TEXTEDIT_TEMP_FOLDER_NAME = "(A Document Being Saved By TextEdit)";

    public AbstractCollectionResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
    }

    @Override
    protected SupportedLock getSupportedLock() {
        return new SupportedLock();
    }

    @Override
    public final boolean isCollection() {
        return true;
    }

    public boolean isValidated() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void spool(OutputContext outputContext) throws IOException {
        if (outputContext.hasStream()) {
            OutputStream outputStream = outputContext.getOutputStream();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<html>").append("<head><title>").append(PlainTextToHtmlConverter.encodeHtmlEntities((String)this.getDisplayName())).append("</title></head>").append("<body>");
            try {
                DavResource parentResource = this.getCollection();
                DavResourceIterator davResourceIterator = this.getMembers();
                if (null != davResourceIterator || null != parentResource) {
                    stringBuffer.append("<ul>");
                    if (null != this.getCollection()) {
                        stringBuffer.append("<li><a href=\"").append(parentResource.getHref()).append("\">../</a></li>");
                    }
                    if (null != davResourceIterator) {
                        while (davResourceIterator.hasNext()) {
                            DavResource children = davResourceIterator.nextResource();
                            stringBuffer.append("<li><a href=\"").append(children.getHref()).append("\">").append(PlainTextToHtmlConverter.encodeHtmlEntities((String)children.getDisplayName())).append("</a></li>");
                        }
                    }
                    stringBuffer.append("</ul>");
                }
                stringBuffer.append("</body></html>");
                byte[] htmlBytes = stringBuffer.toString().getBytes("UTF-8");
                outputContext.setContentLength(htmlBytes.length);
                outputContext.setContentType("text/html");
                outputContext.setModificationTime(this.getModificationTime());
                outputStream.write(htmlBytes);
                outputStream.flush();
            }
            finally {
                IOUtils.closeQuietly((OutputStream)outputStream);
            }
        } else {
            outputContext.setContentLength(0L);
            outputContext.setModificationTime(this.getModificationTime());
        }
    }

    public boolean isTextEditCreatingTempFolder(String resourceName, ConfluenceDavSession confluenceDavSession) {
        return confluenceDavSession.isClientFinder() && resourceName.contentEquals(TEXTEDIT_TEMP_FOLDER_NAME);
    }

    protected abstract Collection<DavResource> getMemberResources();

    @Override
    public final DavResourceIterator getMembers() {
        ArrayList<DavResource> membersResources = new ArrayList<DavResource>();
        boolean isValidated = this.isValidated();
        for (DavResource davResource : this.getMemberResources()) {
            if (!isValidated && !davResource.exists()) continue;
            membersResources.add(davResource);
        }
        return new DavResourceIteratorImpl(membersResources);
    }
}

