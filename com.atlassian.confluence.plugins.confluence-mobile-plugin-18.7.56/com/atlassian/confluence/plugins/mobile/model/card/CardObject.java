/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.model.card.ObjectId;
import java.util.Date;

public interface CardObject {
    public ObjectId getId();

    public static interface Page
    extends CardObject {
        public String getTitle();

        public Person getCreatedBy();

        public Date getCreatedDate();

        public String getTimeToRead();

        public SpaceDto getSpace();

        public boolean isSaved();
    }
}

