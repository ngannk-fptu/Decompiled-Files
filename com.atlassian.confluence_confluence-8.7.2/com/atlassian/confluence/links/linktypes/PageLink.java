/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.links.GenericLinkParser;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;

public class PageLink
extends AbstractPageLink {
    public static PageLink makeTestLink(String linkText, PageContext context) throws ParseException {
        GenericLinkParser parser = new GenericLinkParser(linkText);
        parser.parseAsContentLink();
        return new PageLink(parser, context);
    }

    private PageLink(GenericLinkParser parser, PageContext pageContext) throws ParseException {
        super(parser, pageContext);
        this.destinationPage = new Page();
        this.destinationPage.setTitle(parser.getDestinationTitle() == null ? pageContext.getPageTitle() : parser.getDestinationTitle());
    }

    public PageLink(GenericLinkParser parser, PageContext pageContext, PageManager pageManager) throws ParseException {
        super(parser, pageContext);
        if (!AbstractPage.isValidPageTitle(this.entityName)) {
            throw new ParseException("Invalid page title in link " + this.entityName, 0);
        }
        this.destinationPage = pageManager.getPage(this.spaceKey, this.entityName);
        if (("#" + this.anchor).equals(this.linkBody)) {
            this.linkBody = this.anchor;
        }
        if (this.destinationPage != null) {
            this.setUrlAndTitle(pageContext);
        }
    }

    @Override
    public boolean isOnSamePage(PageContext pageContext) {
        String pageTitle = pageContext.getPageTitle();
        if (!StringUtils.isNotEmpty((CharSequence)pageTitle) || !StringUtils.isNotEmpty((CharSequence)pageContext.getSpaceKey()) || pageContext.getPostingDay() != null) {
            return false;
        }
        return pageTitle.equals(this.destinationPage.getTitle()) && pageContext.getSpaceKey().equals(this.destinationPage.getSpace().getKey());
    }

    @Override
    public ContentEntityObject getDestinationContent() {
        return this.destinationPage;
    }
}

