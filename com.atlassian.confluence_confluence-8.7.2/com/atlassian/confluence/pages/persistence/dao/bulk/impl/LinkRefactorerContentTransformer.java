/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.content.render.xhtml.links.XhtmlLinksUpdater;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageContentTransformer;
import org.springframework.beans.factory.annotation.Qualifier;

public class LinkRefactorerContentTransformer
implements PageContentTransformer {
    private final XhtmlLinksUpdater xhtmlLinksUpdater;

    public LinkRefactorerContentTransformer(@Qualifier(value="linksUpdater") XhtmlLinksUpdater xhtmlLinksUpdater) {
        this.xhtmlLinksUpdater = xhtmlLinksUpdater;
    }

    @Override
    public String transform(String content, Page oldPage, Page newPage) {
        LinksUpdater.PartialReferenceDetails oldDetails = LinksUpdater.PartialReferenceDetails.createReference(oldPage);
        LinksUpdater.PartialReferenceDetails newDetails = LinksUpdater.PartialReferenceDetails.createReference(newPage);
        return this.xhtmlLinksUpdater.updateReferencesInContent(content, oldDetails, newDetails);
    }
}

