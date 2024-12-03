/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.BlogPostReference;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.core.util.DateUtils;
import com.atlassian.renderer.links.GenericLinkParser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class BlogPostLink
extends AbstractPageLink {
    private static final String dayViewTitlePattern = "MMMM d, yyyy";
    private static final String monthViewTitlePattern = "MMMM yyyy";
    private Calendar contextPostingDate;
    private String postingYear;
    private String postingMonth;
    private String postingDay;
    private boolean dateView;

    public BlogPostLink(GenericLinkParser parser, PageContext context, PageManager pageManager) throws ParseException {
        super(parser, context);
        this.contextPostingDate = context.getPostingDay();
        if (!this.entityName.contains("/")) {
            this.destinationPage = pageManager.getBlogPost(context.getSpaceKey(), context.getPageTitle(), context.getPostingDay());
        } else {
            this.parseBlogPostTitle(pageManager);
        }
        if (("#" + this.anchor).equals(this.linkBody)) {
            this.linkBody = this.anchor;
        }
        if (this.entityName == null) {
            this.setUrlAndTitleForDateView(context);
        } else if (this.destinationPage != null) {
            this.setUrlAndTitle(context);
        }
    }

    private void setUrlAndTitleForDateView(PageContext context) {
        String firstArg;
        StringBuilder urlBuf = new StringBuilder("/display/");
        urlBuf.append(this.spaceKey).append("/");
        urlBuf.append(this.makeDatePath());
        this.url = urlBuf.toString();
        String string = firstArg = this.postingDay == null ? this.monthAsString() : this.dayAsString();
        if (this.spaceKey.equals(context.getSpaceKey())) {
            this.setI18nTitle("renderer.news.items.for", Collections.singletonList(firstArg));
        } else {
            this.setI18nTitle("renderer.news.items.for.spacekey", Arrays.asList(firstArg, this.spaceKey));
        }
        this.dateView = true;
    }

    private void parseBlogPostTitle(PageManager pageManager) throws ParseException {
        BlogPostReference blogPostReference = new BlogPostReference(this.entityName, this.spaceKey, pageManager);
        this.postingYear = blogPostReference.getPostingYear();
        this.postingMonth = blogPostReference.getPostingMonth();
        this.postingDay = blogPostReference.getPostingDay();
        this.entityName = blogPostReference.getEntityName();
        this.destinationPage = blogPostReference.getBlogPost();
    }

    public Calendar getPostingDay() {
        if (this.postingDay == null) {
            return this.contextPostingDate;
        }
        return DateUtils.getCalendarDay((int)Integer.parseInt(this.postingYear), (int)(Integer.parseInt(this.postingMonth) - 1), (int)Integer.parseInt(this.postingDay));
    }

    @Override
    protected boolean isOnSamePage(PageContext pageContext) {
        String pageTitle = pageContext.getPageTitle();
        if (!StringUtils.isNotEmpty((CharSequence)pageTitle) || !StringUtils.isNotEmpty((CharSequence)pageContext.getSpaceKey()) || pageContext.getPostingDay() == null) {
            return false;
        }
        if (this.destinationPage == null) {
            return false;
        }
        return pageTitle.equals(this.destinationPage.getTitle()) && pageContext.getSpaceKey().equals(this.destinationPage.getSpace().getKey()) && pageContext.getPostingDay().equals(this.getPostingDay());
    }

    private String monthAsString() {
        Date date = DateUtils.getDateDay((int)Integer.parseInt(this.postingYear), (int)(Integer.parseInt(this.postingMonth) - 1), (int)1);
        return new SimpleDateFormat(monthViewTitlePattern).format(date);
    }

    private String dayAsString() {
        Date date = DateUtils.getDateDay((int)Integer.parseInt(this.postingYear), (int)(Integer.parseInt(this.postingMonth) - 1), (int)Integer.parseInt(this.postingDay));
        return new SimpleDateFormat(dayViewTitlePattern).format(date);
    }

    @Override
    public boolean hasDestination() {
        return this.dateView || super.hasDestination();
    }

    @Override
    public ContentEntityObject getDestinationContent() {
        return this.destinationPage;
    }

    @Override
    public OutgoingLink toOutgoingLink(ContentEntityObject sourceContent) {
        OutgoingLink outgoingLink = new OutgoingLink();
        outgoingLink.setSourceContent(sourceContent);
        outgoingLink.setDestinationSpaceKey(this.getSpaceKey());
        if (this.dateView) {
            outgoingLink.setDestinationPageTitle(this.makeDatePath());
        } else {
            outgoingLink.setDestinationPageTitle(this.makeDatePath() + this.getPageTitle());
        }
        return outgoingLink;
    }

    private String makeDatePath() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.postingYear).append("/");
        buf.append(this.postingMonth).append("/");
        if (this.postingDay != null) {
            buf.append(this.postingDay).append("/");
        }
        return buf.toString();
    }
}

