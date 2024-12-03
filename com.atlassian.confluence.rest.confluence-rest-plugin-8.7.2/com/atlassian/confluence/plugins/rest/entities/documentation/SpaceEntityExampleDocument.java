/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserProperties;
import com.atlassian.confluence.plugins.rest.entities.documentation.ContentEntityExampleDocument;
import com.atlassian.confluence.plugins.rest.entities.documentation.ContentEntityListExampleDocument;
import com.atlassian.confluence.plugins.rest.entities.documentation.DateEntityExampleDocument;
import com.atlassian.confluence.plugins.rest.entities.documentation.LinkExamples;
import java.util.ArrayList;

public class SpaceEntityExampleDocument {
    public static final SpaceEntity EXAMPLE_SPACE_ENTITY = new SpaceEntity();
    public static final SpaceEntityList EXAMPLE_SPACE_ENTITY_LIST;
    public static final SpaceEntity MINIMAL_SPACE_ENTITY;

    static {
        EXAMPLE_SPACE_ENTITY.setDescription("An example of a Confluence space with a tutorial and sample content.");
        EXAMPLE_SPACE_ENTITY.setId("1");
        EXAMPLE_SPACE_ENTITY.setKey("ds");
        EXAMPLE_SPACE_ENTITY.setName("Demonstration Space");
        EXAMPLE_SPACE_ENTITY.setWikiLink("[ds:]");
        EXAMPLE_SPACE_ENTITY.setCreatedDate(DateEntityExampleDocument.DATE_ENTITY);
        EXAMPLE_SPACE_ENTITY.setHome(ContentEntityExampleDocument.DEMO_PAGE);
        EXAMPLE_SPACE_ENTITY.setLastModifiedDate(DateEntityExampleDocument.DATE_ENTITY);
        EXAMPLE_SPACE_ENTITY.addLink(LinkExamples.SELF);
        EXAMPLE_SPACE_ENTITY.addLink(LinkExamples.ALTERNATE);
        EXAMPLE_SPACE_ENTITY.setUserProperties(new SpaceEntityUserProperties(null));
        EXAMPLE_SPACE_ENTITY.setRootPages(ContentEntityListExampleDocument.ONE);
        EXAMPLE_SPACE_ENTITY_LIST = new SpaceEntityList();
        ArrayList<SpaceEntity> spaces = new ArrayList<SpaceEntity>(2);
        spaces.add(EXAMPLE_SPACE_ENTITY);
        spaces.add(EXAMPLE_SPACE_ENTITY);
        MINIMAL_SPACE_ENTITY = new SpaceEntity();
        MINIMAL_SPACE_ENTITY.setName("Demonstration Space");
        MINIMAL_SPACE_ENTITY.setKey("ds");
        MINIMAL_SPACE_ENTITY.addLink(LinkExamples.SELF);
        EXAMPLE_SPACE_ENTITY_LIST.setSpaces(spaces);
    }
}

