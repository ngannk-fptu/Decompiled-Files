/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import org.apache.commons.lang3.StringUtils;

public class PageResourceIdentifierResolver
implements ResourceIdentifierResolver<PageResourceIdentifier, Page> {
    private final PageManager pageManager;

    public PageResourceIdentifierResolver(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public Page resolve(PageResourceIdentifier pageResourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        PageContext context;
        String spaceKey = pageResourceIdentifier.getSpaceKey();
        String pageTitle = pageResourceIdentifier.getTitle();
        PageContext pageContext = context = conversionContext != null ? conversionContext.getPageContext() : null;
        if (StringUtils.isBlank((CharSequence)spaceKey) && context != null) {
            spaceKey = context.getSpaceKey();
        }
        if (StringUtils.isBlank((CharSequence)spaceKey) || StringUtils.isBlank((CharSequence)pageTitle)) {
            throw new CannotResolveResourceIdentifierException(pageResourceIdentifier, "The resource identifier '" + pageResourceIdentifier + "' cannot be resolved. A spaceKey and title are both required to render a page.");
        }
        return this.pageManager.getPage(spaceKey, pageTitle);
    }
}

