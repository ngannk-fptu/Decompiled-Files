/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.actions.PageAware
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.themes.ThemeHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.benryan.webwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.benryan.webwork.util.AttachmentPreviewHelper;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class AttachmentPreviewAction
extends ConfluenceActionSupport
implements PageAware {
    private String fileName;
    private String renderedPreview;
    private AttachmentManager attachmentManager;
    private Attachment attachment;
    private AbstractPage page;

    public void validate() {
        super.validate();
        this.attachment = this.attachmentManager.getAttachment((ContentEntityObject)this.page, this.fileName);
        if (this.attachment == null) {
            this.addActionError("Can't find the attachment with name " + this.fileName + " on page id " + this.page.getId());
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        String spaceKey = this.page.getSpaceKey();
        String pageName = this.page.getTitle();
        String macroText = "{viewfile:name=" + this.fileName + "|page=" + pageName + "|space=" + spaceKey;
        if (this.page instanceof BlogPost) {
            Date date = this.page.getCreationDate();
            DateFormat dateFormat = DateFormat.getDateInstance(3, Locale.US);
            macroText = macroText + "|date=" + dateFormat.format(date);
        }
        macroText = macroText + "}";
        this.renderedPreview = this.getThemeHelper().renderConfluenceMacro(macroText);
        return "success";
    }

    protected ThemeHelper getThemeHelper() {
        return super.getHelper();
    }

    public ThemeHelper getHelper() {
        return new AttachmentPreviewHelper(this, this.attachment);
    }

    @HtmlSafe
    public String getContentHtml() {
        return this.renderedPreview;
    }

    public void setAttachmentManager(@ComponentImport AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public AbstractPage getPage() {
        return this.page;
    }

    public Space getSpace() {
        return this.getPage() == null ? null : this.getPage().getSpace();
    }

    public void setPage(AbstractPage page) {
        this.page = page;
    }

    public boolean isPageRequired() {
        return true;
    }

    public boolean isLatestVersionRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }
}

