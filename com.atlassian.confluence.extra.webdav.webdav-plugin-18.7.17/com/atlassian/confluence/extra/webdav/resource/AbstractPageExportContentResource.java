/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractContentResource;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractPageExportContentResource
extends AbstractContentResource {
    private final PageManager pageManager;
    protected final String spaceKey;
    protected final String pageTitle;
    private Page page;

    public AbstractPageExportContentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    protected Page getPage() {
        if (null == this.page) {
            this.page = this.pageManager.getPage(this.spaceKey, this.pageTitle);
        }
        return this.page;
    }

    protected abstract InputStream getContentInternal() throws IOException;

    protected abstract String getExportSuffix();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File checkWriteToTempFile() throws IOException {
        Page thisPage = this.getPage();
        File exportedContent = new File(GeneralUtil.getConfluenceTempDirectory(), "webdav-" + thisPage.getIdAsString() + this.getExportSuffix());
        if (!exportedContent.exists() || exportedContent.lastModified() < thisPage.getLastModificationDate().getTime()) {
            InputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = this.getContentInternal();
                out = new BufferedOutputStream(new FileOutputStream(exportedContent));
                IOUtils.copy((InputStream)in, (OutputStream)out);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly((InputStream)in);
                throw throwable;
            }
            IOUtils.closeQuietly((OutputStream)out);
            IOUtils.closeQuietly((InputStream)in);
        }
        return exportedContent;
    }

    @Override
    protected InputStream getContent() {
        try {
            return new BufferedInputStream(new FileInputStream(this.checkWriteToTempFile()));
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    protected long getContentLength() {
        try {
            return this.checkWriteToTempFile().length();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public long getModificationTime() {
        return this.getPage().getLastModificationDate().getTime();
    }

    @Override
    protected long getCreationtTime() {
        return this.getPage().getCreationDate().getTime();
    }
}

