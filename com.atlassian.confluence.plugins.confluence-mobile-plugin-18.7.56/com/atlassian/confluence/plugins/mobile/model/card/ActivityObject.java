/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.people.Person;
import java.util.Date;
import java.util.Map;

public interface ActivityObject {
    public Long getId();

    public Date getActionTime();

    public Person getActionBy();

    public static interface ContentActivityObject
    extends ActivityObject {
        public String getExcerpt();

        public Map<String, Object> getProperties();
    }
}

