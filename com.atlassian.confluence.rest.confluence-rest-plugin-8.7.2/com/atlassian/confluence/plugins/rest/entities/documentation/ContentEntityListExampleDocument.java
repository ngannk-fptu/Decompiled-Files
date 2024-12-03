/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.documentation.ContentEntityExampleDocument;
import java.util.ArrayList;

public class ContentEntityListExampleDocument {
    public static final ContentEntityList ONE = new ContentEntityList(2, null);
    public static final ContentEntityList EXPANDED = new ContentEntityList(2, null);

    static {
        ArrayList<ContentEntity> list = new ArrayList<ContentEntity>(2);
        list.add(ContentEntityExampleDocument.DEMO_PAGE);
        list.add(ContentEntityExampleDocument.DEMO_PAGE);
        EXPANDED.setContents(list);
    }
}

