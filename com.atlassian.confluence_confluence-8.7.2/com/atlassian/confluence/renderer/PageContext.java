/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchContext$Builder
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.LinkContext
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.content.render.xhtml.ElementIdCreator;
import com.atlassian.confluence.content.render.xhtml.HtmlElementIdCreator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.util.ContentUtils;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.LinkContext;
import com.atlassian.util.concurrent.Timeout;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageContext
extends RenderContext
implements LinkContext {
    private String spaceKey;
    private Calendar postingDay;
    private ContentEntityObject entity;
    private ContentEntityObject latestVersionOfEntity;
    private PageContext originalContext;
    private final io.atlassian.util.concurrent.Timeout timeout;
    private final ElementIdCreator elementIdCreator;
    private String outputDeviceType;

    @Deprecated
    public static PageContext contextWithTimeout(ContentEntityObject entity, Timeout timeout) {
        return new PageContext(entity, null, ConcurrentConversionUtil.toIoTimeout((Timeout)timeout));
    }

    public static PageContext newContextWithTimeout(ContentEntityObject entity, io.atlassian.util.concurrent.Timeout timeout) {
        return new PageContext(entity, null, timeout);
    }

    public PageContext() {
        this((String)null);
    }

    public PageContext(String spaceKey) {
        this.spaceKey = spaceKey;
        this.timeout = PageContext.minimumTimeout();
        this.elementIdCreator = new HtmlElementIdCreator();
        this.setOutputDeviceType("desktop");
    }

    public PageContext(ContentEntityObject entity) {
        this(entity, null, null);
    }

    @Deprecated
    public PageContext(ContentEntityObject entity, PageContext previousContext) {
        this(entity, previousContext, null);
    }

    private PageContext(ContentEntityObject entity, PageContext previousContext, io.atlassian.util.concurrent.Timeout timeout) {
        super(previousContext == null ? null : previousContext.getRenderedContentStore());
        this.entity = entity;
        io.atlassian.util.concurrent.Timeout timeout2 = timeout != null ? timeout : (this.timeout = previousContext != null ? previousContext.timeout() : PageContext.minimumTimeout());
        if (previousContext != null) {
            this.originalContext = previousContext.getOriginalContext();
            this.setAttachmentsPath(previousContext.getAttachmentsPath());
            this.setImagePath(previousContext.getImagePath());
            this.setSiteRoot(previousContext.getSiteRoot());
            this.setBaseUrl(previousContext.getBaseUrl());
            this.setLinkRenderer(previousContext.getLinkRenderer());
            this.setEmbeddedResourceRenderer(previousContext.getEmbeddedResourceRenderer());
            this.pushRenderMode(previousContext.getRenderMode());
            this.setOutputType(previousContext.getOutputType());
            this.setOutputDeviceType(previousContext.getOutputDeviceType());
            this.elementIdCreator = previousContext.getElementIdCreator();
        } else {
            this.elementIdCreator = new HtmlElementIdCreator();
            this.setOutputDeviceType("desktop");
        }
        if (entity != null) {
            this.latestVersionOfEntity = (ContentEntityObject)entity.getLatestVersion();
        }
        this.addParam("com.atlassian.renderer.embedded.placeholder.image.name", "/icons/attachments/image_16.png");
    }

    @Deprecated
    public static Timeout createMinimumTimeout() {
        return ConcurrentConversionUtil.toComTimeout((io.atlassian.util.concurrent.Timeout)PageContext.minimumTimeout());
    }

    public static io.atlassian.util.concurrent.Timeout minimumTimeout() {
        return io.atlassian.util.concurrent.Timeout.getMillisTimeout((long)1L, (TimeUnit)TimeUnit.HOURS);
    }

    @Deprecated
    public Timeout getTimeout() {
        return ConcurrentConversionUtil.toComTimeout((io.atlassian.util.concurrent.Timeout)this.timeout);
    }

    public io.atlassian.util.concurrent.Timeout timeout() {
        return this.timeout;
    }

    public String getOutputDeviceType() {
        return this.outputDeviceType;
    }

    public void setOutputDeviceType(String type) {
        this.outputDeviceType = type;
        this.addParam("output-device-type", type);
    }

    public PageContext getOriginalContext() {
        if (this.originalContext == null) {
            return this;
        }
        return this.originalContext;
    }

    public String getSpaceKey() {
        return this.latestVersionOfEntity != null ? ContentUtils.getSpaceKeyFromCeo(this.latestVersionOfEntity) : this.spaceKey;
    }

    public @Nullable String getPageTitle() {
        if (this.latestVersionOfEntity instanceof Comment) {
            ContentEntityObject container = ((Comment)this.latestVersionOfEntity).getContainer();
            return container != null ? container.getTitle() : null;
        }
        return this.entity != null ? this.entity.getTitle() : null;
    }

    public Calendar getPostingDay() {
        if (this.postingDay == null && this.latestVersionOfEntity instanceof BlogPost) {
            this.postingDay = BlogPost.toCalendar(this.latestVersionOfEntity.getCreationDate());
        }
        return this.postingDay;
    }

    public ContentEntityObject getEntity() {
        return this.entity;
    }

    public ElementIdCreator getElementIdCreator() {
        return this.elementIdCreator;
    }

    public SearchContext.Builder toSearchContext() {
        ContentId contentId = this.getEntity() != null ? this.getEntity().getContentId() : null;
        return SearchContext.builder().spaceKey(this.getSpaceKey()).contentId(contentId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageContext)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PageContext pageContext = (PageContext)((Object)o);
        if (this.entity != null ? !this.entity.equals(pageContext.entity) : pageContext.entity != null) {
            return false;
        }
        if (this.originalContext != null ? !this.originalContext.equals((Object)pageContext.originalContext) : pageContext.originalContext != null) {
            return false;
        }
        return this.spaceKey != null ? this.spaceKey.equals(pageContext.spaceKey) : pageContext.spaceKey == null;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.spaceKey != null ? this.spaceKey.hashCode() : 0);
        result = 29 * result + (this.entity != null ? this.entity.hashCode() : 0);
        result = 29 * result + (this.originalContext != null ? this.originalContext.hashCode() : 0);
        return result;
    }
}

