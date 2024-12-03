/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.service.IdContentLocator;
import com.atlassian.confluence.content.service.RenderContentCommand;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderContentCommandImpl
extends AbstractServiceCommand
implements RenderContentCommand {
    private static final Logger log = LoggerFactory.getLogger(RenderContentCommandImpl.class);
    private final PermissionManager permissionManager;
    private final FormatConverter formatConverter;
    private final ConversionContextOutputType conversionContextOutputType;
    private final PageTemplateManager pageTemplateManager;
    private final SpaceLocator spaceLocator;
    private final IdContentLocator contentLocator;
    private final String content;
    private final String contentType;
    private Space space;
    private ConfluenceEntityObject entity;
    private String renderedContent;

    public RenderContentCommandImpl(PermissionManager permissionManager, IdContentLocator entityLocator, SpaceLocator spaceLocator, String contentType, String content, FormatConverter formatConverter, ConversionContextOutputType conversionContextOutputType, PageTemplateManager pageTemplateManager) {
        this.permissionManager = permissionManager;
        this.contentLocator = entityLocator;
        this.spaceLocator = spaceLocator;
        this.contentType = contentType;
        this.content = content;
        this.formatConverter = formatConverter;
        this.conversionContextOutputType = conversionContextOutputType;
        this.pageTemplateManager = pageTemplateManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        PageContext renderContext;
        try {
            this.getContentType();
        }
        catch (IllegalArgumentException e) {
            validator.addValidationError("render.unknown.content.type", this.contentType);
        }
        if (this.getSpace() == null && !this.isTemplate()) {
            validator.addValidationError("space.doesnt.exist", new Object[0]);
        }
        if (this.contentLocator.getContentId() != 0L && this.getEntity() == null && !this.isTemplate()) {
            validator.addValidationError("content.doesnt.exist", new Object[0]);
        }
        ConfluenceEntityObject confluenceEntityObject = this.contentLocator.getEntity();
        String originalContent = "";
        ContentEntityObject conversionContextEntity = null;
        if (!(confluenceEntityObject instanceof ContentEntityObject)) {
            renderContext = this.space != null ? new PageContext(this.space.getKey()) : new PageContext();
        } else {
            ContentEntityObject ceo = (ContentEntityObject)confluenceEntityObject;
            renderContext = ceo.toPageContext();
            DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
            conversionContextEntity = conversionContext.getEntity();
            if (conversionContextEntity != null) {
                originalContent = conversionContextEntity.getBodyAsString();
            }
        }
        if (this.isTemplate()) {
            PageTemplate pageTemplate = this.pageTemplateManager.getPageTemplate(this.contentLocator.getContentId());
            if (pageTemplate == null) {
                pageTemplate = new PageTemplate();
                pageTemplate.setSpace(this.space);
            }
            renderContext = new PageTemplateContext(pageTemplate);
            renderContext.setOutputType("preview");
            renderContext.addParam("com.atlassian.confluence.plugins.templates", true);
            renderContext.addParam("com.atlassian.confluence.plugins.templates.input.disable", true);
        }
        try {
            if (conversionContextEntity != null && !originalContent.equals(this.content)) {
                conversionContextEntity.setBodyAsString(this.content);
            }
            if (this.conversionContextOutputType != null) {
                renderContext.setOutputType(this.conversionContextOutputType.value());
            }
            String storageFormat = this.formatConverter.convertToStorageFormat(this.content, renderContext);
            this.renderedContent = this.formatConverter.convertToViewFormat(storageFormat, renderContext);
        }
        catch (XhtmlParsingException ex) {
            validator.addValidationError("content.xhtml.parse.failed", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        }
        catch (XhtmlException ex) {
            log.error("Error generating preview.", (Throwable)ex);
            validator.addValidationError("editor.preview.error", new Object[0]);
        }
        finally {
            if (conversionContextEntity != null && !originalContent.equals(this.content)) {
                conversionContextEntity.setBodyAsString(originalContent);
            }
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.isNewContent() || this.isTemplate()) {
            return this.permissionManager.hasCreatePermission(this.getCurrentUser(), (Object)this.getSpace(), this.getContentType().getContentClass());
        }
        return this.permissionManager.hasPermission(this.getCurrentUser(), Permission.EDIT, this.getEntity());
    }

    private boolean isTemplate() {
        return ContentType.TEMPLATE.equals((Object)this.getContentType());
    }

    private boolean isNewContent() {
        return this.contentLocator.getContentId() == 0L || this.getContentType() == ContentType.COMMENT && this.getEntity() instanceof AbstractPage;
    }

    @Override
    protected void executeInternal() {
    }

    @Override
    public String getRenderedContent() {
        return this.renderedContent;
    }

    private ContentType getContentType() {
        return ContentType.valueOf(this.contentType.toUpperCase());
    }

    private ConfluenceEntityObject getEntity() {
        if (this.entity == null) {
            this.entity = this.contentLocator.getEntity();
        }
        return this.entity;
    }

    private Space getSpace() {
        if (this.space == null) {
            this.space = this.spaceLocator.getSpace();
        }
        return this.space;
    }

    private static enum ContentType {
        PAGE("page"),
        BLOGPOST("blogpost"),
        COMMENT("comment"),
        TEMPLATE("template");

        private final String value;

        private ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public Class getContentClass() {
            if (PAGE.equals((Object)this)) {
                return Page.class;
            }
            if (BLOGPOST.equals((Object)this)) {
                return BlogPost.class;
            }
            if (COMMENT.equals((Object)this)) {
                return Comment.class;
            }
            if (TEMPLATE.equals((Object)this)) {
                return PageTemplate.class;
            }
            return null;
        }
    }
}

