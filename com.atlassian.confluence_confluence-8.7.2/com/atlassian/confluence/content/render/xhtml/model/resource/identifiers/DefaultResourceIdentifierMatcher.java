/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierMatcher;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;

public class DefaultResourceIdentifierMatcher
implements ResourceIdentifierMatcher {
    @Override
    public boolean matches(ContentEntityObject ceo, ResourceIdentifier ri) {
        if (ceo instanceof Page) {
            return this.matches((Page)ceo, ri);
        }
        if (ceo instanceof BlogPost) {
            return this.matches((BlogPost)ceo, ri);
        }
        if (ceo instanceof Draft) {
            return this.matches((Draft)ceo, ri);
        }
        if (ceo instanceof Comment) {
            return this.matches((Comment)ceo, ri);
        }
        return false;
    }

    protected boolean matches(Page page, ResourceIdentifier ri) {
        if (!(ri instanceof PageResourceIdentifier)) {
            return ri instanceof ContentEntityResourceIdentifier && ((ContentEntityResourceIdentifier)ri).getContentId() == page.getId();
        }
        PageResourceIdentifier pageRi = (PageResourceIdentifier)ri;
        String spaceKey = (String)StringUtils.defaultIfEmpty((CharSequence)pageRi.getSpaceKey(), (CharSequence)page.getSpaceKey());
        return StringUtils.equals((CharSequence)spaceKey, (CharSequence)page.getSpaceKey()) && StringUtils.equals((CharSequence)pageRi.getTitle(), (CharSequence)page.getTitle());
    }

    protected boolean matches(BlogPost blog, ResourceIdentifier ri) {
        if (!(ri instanceof BlogPostResourceIdentifier)) {
            return false;
        }
        BlogPostResourceIdentifier blogRi = (BlogPostResourceIdentifier)ri;
        String spaceKey = (String)StringUtils.defaultIfEmpty((CharSequence)blogRi.getSpaceKey(), (CharSequence)blog.getSpaceKey());
        if (!StringUtils.equals((CharSequence)spaceKey, (CharSequence)blog.getSpaceKey()) || !StringUtils.equals((CharSequence)blogRi.getTitle(), (CharSequence)blog.getTitle())) {
            return false;
        }
        Calendar blogRiCalendar = blogRi.getPostingDay();
        Calendar blogCalendar = blog.getPostingCalendarDate();
        if (blogCalendar == null && blogRiCalendar == null) {
            return true;
        }
        if (blogCalendar == null || blogRiCalendar == null) {
            return false;
        }
        if (blogCalendar.get(6) != blogRiCalendar.get(6)) {
            return false;
        }
        return blogCalendar.get(1) == blogRiCalendar.get(1);
    }

    protected boolean matches(Draft draft, ResourceIdentifier ri) {
        if (!(ri instanceof DraftResourceIdentifier)) {
            return false;
        }
        DraftResourceIdentifier draftRi = (DraftResourceIdentifier)ri;
        return draft.getId() == draftRi.getDraftId();
    }

    protected boolean matches(Comment comment, ResourceIdentifier ri) {
        ContentEntityObject owner = comment.getContainer();
        return this.matches(owner, ri);
    }
}

