/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import java.text.ParseException;
import java.util.Calendar;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class PageLinkParser {
    private final SpaceManager spaceManager;
    private final PageManager pageManager;

    public PageLinkParser(SpaceManager spaceManager, PageManager pageManager) {
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
    }

    public ResourceIdentifier parse(String linkText, String spaceKey) {
        String linkedSpaceKey;
        if (linkText == null) {
            return null;
        }
        String destinationTitle = linkText;
        int index = linkText.indexOf(58);
        if (index >= 0 && this.spaceManager.getSpace(linkedSpaceKey = linkText.substring(0, index)) != null) {
            destinationTitle = linkText.substring(index + 1);
            spaceKey = linkedSpaceKey;
        }
        if (BlogPostResourceIdentifier.isBlogPostLink((String)destinationTitle)) {
            try {
                return BlogPostResourceIdentifier.newInstanceFromLink((String)destinationTitle, (String)spaceKey);
            }
            catch (ParseException e) {
                return null;
            }
        }
        if (StringUtils.isNotBlank((CharSequence)destinationTitle)) {
            return new PageResourceIdentifier(spaceKey, destinationTitle);
        }
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            return new SpaceResourceIdentifier(spaceKey);
        }
        return null;
    }

    public RoadmapPageLink resolveConfluenceLink(String linkText, String currentSpaceKey) {
        linkText = StringEscapeUtils.unescapeHtml4((String)linkText);
        ResourceIdentifier ri = this.parse(linkText, currentSpaceKey);
        String contentId = "";
        String spaceKey = "";
        String pageTitle = "";
        String type = "";
        RoadmapPageLink pageLink = new RoadmapPageLink();
        if (ri instanceof PageResourceIdentifier) {
            type = "page";
            spaceKey = ((PageResourceIdentifier)ri).getSpaceKey();
            Page page = this.pageManager.getPage(spaceKey, pageTitle = ((PageResourceIdentifier)ri).getTitle());
            contentId = page != null ? String.valueOf(page.getId()) : pageLink.getId();
        } else if (ri instanceof BlogPostResourceIdentifier) {
            Calendar postingDay;
            type = "blogpost";
            spaceKey = ((BlogPostResourceIdentifier)ri).getSpaceKey();
            BlogPost blogPost = this.pageManager.getBlogPost(spaceKey, pageTitle = ((BlogPostResourceIdentifier)ri).getTitle(), postingDay = ((BlogPostResourceIdentifier)ri).getPostingDay());
            contentId = blogPost != null ? String.valueOf(blogPost.getId()) : pageLink.getId();
        }
        pageLink.setId(contentId);
        pageLink.setSpaceKey(spaceKey);
        pageLink.setTitle(pageTitle);
        pageLink.setType(type);
        pageLink.setWikiLink(StringUtils.join((Object[])new String[]{"[", linkText, "]"}));
        return pageLink;
    }
}

