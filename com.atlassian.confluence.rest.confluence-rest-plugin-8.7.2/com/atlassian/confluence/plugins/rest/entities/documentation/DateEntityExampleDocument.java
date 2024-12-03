/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.DateEntity;

public class DateEntityExampleDocument {
    public static final DateEntity DATE_ENTITY = new DateEntity();

    static {
        DATE_ENTITY.setDate("2009-11-24T13:09:46+1100");
        DATE_ENTITY.setFriendly("Nov 24");
    }
}

