/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.viewfile.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.DelegateFileMarshaller;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ViewFileMacro
extends BaseMacro
implements StreamableMacro,
EditorImagePlaceholder {
    private static final String SPACE_KEY = "space";
    private static final String PAGE_KEY = "page";
    private static final String NAME_KEY = "name";
    private static final String DATE_KEY = "date";
    private final DelegateFileMarshaller delegateFileMarshaller;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final AttachmentManager attachmentManager;

    public ViewFileMacro(DelegateFileMarshaller delegateFileMarshaller, PageManager pageManager, PermissionManager permissionManager, AttachmentManager attachmentManager) {
        this.delegateFileMarshaller = delegateFileMarshaller;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.attachmentManager = attachmentManager;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> params, ConversionContext conversionContext) {
        Attachment attachment = this.getAttachment(params, conversionContext.getPageContext());
        return this.delegateFileMarshaller.getImagePlaceholder(attachment, params);
    }

    public String execute(Map<String, String> params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        Attachment attachment = this.getAttachment(params, conversionContext.getPageContext());
        try {
            StringWriter writer = new StringWriter();
            this.delegateFileMarshaller.getRenderedContentStreamable(attachment, params, conversionContext).writeTo((Writer)writer);
            return ((Object)writer).toString();
        }
        catch (IOException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)params, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public Streamable executeToStream(Map<String, String> params, Streamable streamable, ConversionContext conversionContext) throws MacroExecutionException {
        Attachment attachment = this.getAttachment(params, conversionContext.getPageContext());
        return this.delegateFileMarshaller.getRenderedContentStreamable(attachment, params, conversionContext);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.INLINE;
    }

    private Attachment getAttachment(Map params, PageContext context) {
        Attachment attachment;
        ContentEntityObject ceo;
        String spaceKey = (String)StringUtils.defaultIfEmpty((CharSequence)((String)params.get(SPACE_KEY)), (CharSequence)"");
        String pageTitle = (String)StringUtils.defaultIfEmpty((CharSequence)((String)params.get(PAGE_KEY)), (CharSequence)"");
        String fileName = (String)params.get(NAME_KEY);
        if (StringUtils.isBlank((CharSequence)spaceKey) && StringUtils.isBlank((CharSequence)pageTitle)) {
            ceo = context.getEntity();
            if (ceo instanceof Comment) {
                ContentEntityObject container = ((Comment)ceo).getContainer();
                if (container instanceof Attachment) {
                    ceo = ((Attachment)container).getContainer();
                } else if (container instanceof AbstractPage) {
                    ceo = container;
                }
            }
        } else {
            spaceKey = (String)StringUtils.defaultIfEmpty((CharSequence)spaceKey, (CharSequence)context.getSpaceKey());
            String date = (String)StringUtils.defaultIfEmpty((CharSequence)((String)params.get(DATE_KEY)), (CharSequence)"");
            if (StringUtils.isBlank((CharSequence)date)) {
                Page page = this.pageManager.getPage(spaceKey, pageTitle);
                if (page == null) {
                    return null;
                }
                ceo = page;
            } else {
                BlogPost blogPost = this.pageManager.getBlogPost(spaceKey, pageTitle, BlogPost.getCalendarFromDatePath((String)("/" + date + pageTitle)));
                if (blogPost == null) {
                    return null;
                }
                ceo = blogPost;
            }
        }
        if ((attachment = !ceo.isLatestVersion() && ceo.getLatestVersion() instanceof ContentEntityObject ? this.attachmentManager.getAttachment((ContentEntityObject)ceo.getLatestVersion(), fileName) : this.attachmentManager.getAttachment(ceo, fileName)) == null) {
            return null;
        }
        if (!this.hasPermissions(attachment)) {
            return null;
        }
        return attachment;
    }

    private boolean hasPermissions(Attachment attachment) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)attachment);
    }
}

