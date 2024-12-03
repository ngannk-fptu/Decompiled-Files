/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyType
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.plugins.rest.entities.ContentBodyEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.documentation.DateEntityExampleDocument;
import com.atlassian.confluence.plugins.rest.entities.documentation.LinkExamples;
import com.atlassian.confluence.plugins.rest.entities.documentation.SpaceEntityExampleDocument;

public class ContentEntityExampleDocument {
    public static final ContentEntity DEMO_PAGE = new ContentEntity();
    public static final ContentEntity DEMO_PAGE_WITH_BODY;

    static {
        DEMO_PAGE.setType("page");
        DEMO_PAGE.setTitle("Home");
        DEMO_PAGE.setWikiLink("[ds:Home]");
        DEMO_PAGE.setLastModifiedDate(DateEntityExampleDocument.DATE_ENTITY);
        DEMO_PAGE.setCreatedDate(DateEntityExampleDocument.DATE_ENTITY);
        DEMO_PAGE.addLink(LinkExamples.SELF);
        DEMO_PAGE.addLink(LinkExamples.ALTERNATE);
        DEMO_PAGE.addLink(LinkExamples.ALTERNATE_PDF);
        DEMO_PAGE.setSpace(SpaceEntityExampleDocument.MINIMAL_SPACE_ENTITY);
        DEMO_PAGE_WITH_BODY = new ContentEntity();
        DEMO_PAGE_WITH_BODY.setType("page");
        DEMO_PAGE_WITH_BODY.setTitle("Home");
        DEMO_PAGE_WITH_BODY.setWikiLink("[ds:Home]");
        DEMO_PAGE_WITH_BODY.setLastModifiedDate(DateEntityExampleDocument.DATE_ENTITY);
        DEMO_PAGE_WITH_BODY.setCreatedDate(DateEntityExampleDocument.DATE_ENTITY);
        DEMO_PAGE_WITH_BODY.addLink(LinkExamples.SELF);
        DEMO_PAGE_WITH_BODY.addLink(LinkExamples.ALTERNATE);
        DEMO_PAGE_WITH_BODY.addLink(LinkExamples.ALTERNATE_PDF);
        DEMO_PAGE_WITH_BODY.setSpace(SpaceEntityExampleDocument.MINIMAL_SPACE_ENTITY);
        DEMO_PAGE_WITH_BODY.setContentBody(new ContentBodyEntity("h1. Demo Area\n\nConfluence is Enterprise [wiki software|http://www.atlassian.com/software/confluence] which extends beyond average wikis. Confluence is available for both [download|http://www.atlassian.com/software/confluence/ConfluenceDownloadCenter.jspa] &\n [hosted|http://www.atlassian.com/software/confluence/hosted.jsp] versions.\n\n\nTo get an quick overview, we recommend browsing through the tours suggested above.\n", BodyType.WIKI));
    }
}

