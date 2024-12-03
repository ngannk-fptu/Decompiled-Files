/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;

public class BlogPostResourceIdentifierResolver
implements ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> {
    private final PageManager pageManager;

    public BlogPostResourceIdentifierResolver(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public BlogPost resolve(BlogPostResourceIdentifier blogResourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        PageContext context;
        String spaceKey = blogResourceIdentifier.getSpaceKey();
        String blogTitle = blogResourceIdentifier.getTitle();
        Calendar postingDay = blogResourceIdentifier.getPostingDay();
        PageContext pageContext = context = conversionContext != null ? conversionContext.getPageContext() : null;
        if (StringUtils.isBlank((CharSequence)spaceKey) && context != null) {
            spaceKey = context.getSpaceKey();
        }
        if (StringUtils.isBlank((CharSequence)spaceKey) || StringUtils.isBlank((CharSequence)blogTitle) || postingDay == null) {
            throw new CannotResolveResourceIdentifierException(blogResourceIdentifier, "The resource identifier '" + blogResourceIdentifier + "' cannot be resolved. A spaceKey, title and posting day are all required to render a blog post.");
        }
        return this.pageManager.getBlogPost(spaceKey, blogTitle, postingDay);
    }
}

