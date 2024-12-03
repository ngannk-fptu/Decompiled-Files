/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 *  com.atlassian.renderer.v2.components.BackslashEscapeRendererComponent
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.content.render.xhtml.HtmlElementIdCreator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.linktypes.AbstractContentEntityLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.v2.components.BackslashEscapeRendererComponent;
import java.text.ParseException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPageLink
extends AbstractContentEntityLink {
    protected String spaceKey;
    protected @Nullable AbstractPage destinationPage;
    protected String anchor;
    private boolean escapeLinkBody;

    public AbstractPageLink(GenericLinkParser parser, PageContext context) throws ParseException {
        super(parser);
        String destinationTitle = parser.getDestinationTitle();
        this.entityName = StringUtils.isNotEmpty((CharSequence)destinationTitle) ? destinationTitle : context.getPageTitle();
        String spaceKey = parser.getSpaceKey();
        this.spaceKey = StringUtils.isNotEmpty((CharSequence)spaceKey) ? spaceKey : context.getSpaceKey();
        this.anchor = parser.getAnchor();
        if (this.noContextForPage(context)) {
            throw new ParseException("Not enough information to know where to link", 0);
        }
        if (parser.getLinkBody() == null) {
            this.escapeLinkBody = true;
        }
    }

    protected void setUrlAndTitle(PageContext pageContext) {
        if (StringUtils.isNotEmpty((CharSequence)this.anchor)) {
            this.setUrlAndTitleWithAnchor(pageContext, this.anchor);
        } else if (this.destinationPage != null) {
            this.url = this.destinationPage.getUrlPath();
            this.setTitle(this.destinationPage.getTitle());
        }
    }

    private void setUrlAndTitleWithAnchor(PageContext pageContext, String anchor) {
        if (this.isOnSamePage(pageContext)) {
            this.url = "#" + this.getAnchor(pageContext);
            this.relativeUrl = false;
        } else {
            this.url = this.destinationPage.getUrlPath() + "#" + this.getAnchor(pageContext);
        }
        this.setI18nTitle("renderer.title.with.anchor", Arrays.asList(anchor, this.destinationPage.getTitle()));
    }

    protected abstract boolean isOnSamePage(PageContext var1);

    private boolean noContextForPage(PageContext context) {
        return !StringUtils.isNotEmpty((CharSequence)this.spaceKey) && !StringUtils.isNotEmpty((CharSequence)context.getSpaceKey()) || !StringUtils.isNotEmpty((CharSequence)this.entityName) && !StringUtils.isNotEmpty((CharSequence)context.getPageTitle());
    }

    public String getAnchor(PageContext pageContext) {
        if (!StringUtils.isNotEmpty((CharSequence)this.anchor)) {
            return null;
        }
        if (this.destinationPage != null && this.destinationPage.equals(pageContext.getEntity())) {
            return AbstractPageLink.generateAnchor(new PageContext(pageContext.getOriginalContext().getEntity()), this.anchor);
        }
        return AbstractPageLink.generateAnchor(new PageContext(this.destinationPage), this.anchor);
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getPageTitle() {
        return this.entityName;
    }

    @Override
    public @Nullable ContentEntityObject getDestinationContent() {
        return this.destinationPage;
    }

    public OutgoingLink toOutgoingLink(ContentEntityObject sourceContent) {
        OutgoingLink outgoingLink = new OutgoingLink();
        outgoingLink.setDestinationPageTitle(this.getPageTitle());
        outgoingLink.setDestinationSpaceKey(this.getSpaceKey());
        outgoingLink.setSourceContent(sourceContent);
        return outgoingLink;
    }

    @Override
    public String getLinkBody() {
        if (this.escapeLinkBody) {
            return BackslashEscapeRendererComponent.escapeWiki((String)super.getLinkBody());
        }
        return super.getLinkBody();
    }

    public static String generateUnencodedAnchor(PageContext context, String anchor) {
        context = context.getOriginalContext();
        Object prefix = "";
        String pageTitle = context.getPageTitle();
        String spaceKey = context.getSpaceKey();
        if (pageTitle != null) {
            prefix = pageTitle.replaceAll("[-#]", "") + "-";
        } else if (spaceKey != null) {
            prefix = spaceKey + "-";
        }
        return HtmlElementIdCreator.convertToIdHtml5((String)prefix + anchor);
    }

    public static String generateAnchor(PageContext context, String anchor) {
        return HtmlUtil.htmlEncode(AbstractPageLink.generateUnencodedAnchor(context, anchor));
    }

    public static String generateUniqueAnchor(PageContext context, String anchor) {
        return HtmlUtil.htmlEncode(context.getElementIdCreator().generateId(AbstractPageLink.generateUnencodedAnchor(context, anchor)));
    }
}

