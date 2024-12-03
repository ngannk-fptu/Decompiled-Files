/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.UserHistoryList;
import com.atlassian.confluence.plugins.rest.entities.UserSessionEntity;
import com.atlassian.confluence.plugins.rest.entities.documentation.ContentEntityListExampleDocument;

public class UserSessionEntityExampleDocument {
    public static final UserSessionEntity USER_SESSION_ENTITY = new UserSessionEntity(ContentEntityListExampleDocument.EXPANDED);
    public static final UserHistoryList USER_HISTORY_LIST = new UserHistoryList();

    static {
        USER_HISTORY_LIST.getContents().addAll(ContentEntityListExampleDocument.EXPANDED.getContents());
    }
}

