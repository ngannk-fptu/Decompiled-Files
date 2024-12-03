/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator$MigrationResult
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugins.conversion.confluence.dom.ImportContext
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo
 *  com.atlassian.plugins.conversion.confluence.parser.ConfluenceImage
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.plugins.conversion.dom;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.conversion.dom.ConfluenceUtilities;
import com.atlassian.plugins.conversion.confluence.dom.ImportContext;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo;
import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImage;
import com.atlassian.renderer.RenderContext;
import com.atlassian.spring.container.ContainerManager;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DefaultImportContext
implements ImportContext {
    protected AbstractPage page;
    protected AbstractPage oldPage;
    protected final AttachmentManager attachmentManager;
    protected final BookmarkInfo bookmarks;
    protected final PageManager pageManager;
    private final Dimension maxImportImageSize;

    public DefaultImportContext(PageManager pageManager, AbstractPage page, AbstractPage oldPage, AttachmentManager attachmentManager, BookmarkInfo bookmarks, Dimension maxImportImageSize) {
        this.pageManager = pageManager;
        this.page = page;
        this.oldPage = oldPage;
        this.attachmentManager = attachmentManager;
        this.bookmarks = bookmarks;
        this.maxImportImageSize = maxImportImageSize;
    }

    public String createHyperlinkReference(StringBuffer codeBuf) {
        Object ref = this.parseFieldCode(codeBuf);
        if (ref == null) {
            return "";
        }
        if (this.bookmarks.get((String)(ref = ((String)ref).replace("\\\\", "\\"))) != null) {
            ref = "#" + (String)ref;
        } else if (((String)ref).startsWith("//")) {
            ref = "http:" + (String)ref;
        }
        return ref;
    }

    public boolean imageExists(ConfluenceImage parser) {
        return ConfluenceUtilities.findImage(this.pageManager, this.attachmentManager, this.page, parser.getSpaceKey(), parser.getPageName(), parser.getImgName()) != null;
    }

    public void importImage(String fileName, String contentType, byte[] buf) throws IOException {
        if (this.page.getAttachmentNamed(fileName) == null) {
            Attachment imgFile = new Attachment(fileName, contentType, (long)buf.length, "imported from a Word document");
            this.page.addAttachment(imgFile);
            this.attachmentManager.saveAttachment(imgFile, null, (InputStream)new ByteArrayInputStream(buf));
        }
    }

    public Dimension getMaxImportedImageSize() {
        return this.maxImportImageSize;
    }

    protected String parseFieldCode(StringBuffer codeBuf) {
        int linkStart = codeBuf.indexOf("\"");
        int linkEnd = -1;
        if (linkStart != -1 && linkStart < codeBuf.length() - 1) {
            linkEnd = codeBuf.indexOf("\"", linkStart + 1);
            ++linkStart;
        } else {
            linkStart = codeBuf.indexOf("HYPERLINK");
            if (linkStart != -1) {
                linkStart += 9;
                while (linkStart < codeBuf.length() && Character.isWhitespace(codeBuf.charAt(linkStart))) {
                    ++linkStart;
                }
            }
            linkEnd = codeBuf.length();
        }
        Object ref = null;
        if (linkStart != -1 && linkEnd != -1 && linkStart != linkEnd) {
            ref = codeBuf.substring(linkStart, linkEnd);
            int bookmarkIdx = codeBuf.indexOf("\\l", linkEnd + 1);
            if (bookmarkIdx != -1 && (linkStart = codeBuf.indexOf("\"", bookmarkIdx)) != -1 && linkStart < codeBuf.length() - 1 && (linkEnd = codeBuf.indexOf("\"", linkStart + 1)) != -1 && linkStart != linkEnd) {
                ref = (String)ref + "#" + codeBuf.substring(linkStart + 1, linkEnd);
            }
        }
        return ref;
    }

    public void finish(StringBuilder out) {
        if (this.page == null) {
            return;
        }
        ExceptionTolerantMigrator migrator = (ExceptionTolerantMigrator)ContainerManager.getComponent((String)"wikiToXhtmlMigrator");
        ExceptionTolerantMigrator.MigrationResult migrationResult = migrator.migrate(out.toString(), (ConversionContext)new DefaultConversionContext((RenderContext)this.page.toPageContext()));
        String migratedBody = migrationResult.getContent();
        this.page.setBodyAsString(migratedBody);
        if (this.oldPage == null) {
            this.pageManager.saveContentEntity((ContentEntityObject)this.page, null);
        } else {
            this.pageManager.saveContentEntity((ContentEntityObject)this.page, (ContentEntityObject)this.oldPage, null);
            this.oldPage = null;
        }
    }
}

