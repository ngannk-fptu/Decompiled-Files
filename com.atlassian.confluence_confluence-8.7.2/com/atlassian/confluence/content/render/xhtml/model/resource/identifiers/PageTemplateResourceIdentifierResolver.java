/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;

public class PageTemplateResourceIdentifierResolver
implements ResourceIdentifierResolver<PageTemplateResourceIdentifier, PageTemplate> {
    private PageTemplateDao pageTemplateDao;

    public PageTemplateResourceIdentifierResolver(PageTemplateDao pageTemplateDao) {
        this.pageTemplateDao = pageTemplateDao;
    }

    @Override
    public PageTemplate resolve(PageTemplateResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        PageTemplate template = this.pageTemplateDao.getById(resourceIdentifier.getTemplateId());
        return template != null ? template : new PageTemplate();
    }
}

