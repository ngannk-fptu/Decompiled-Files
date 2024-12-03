/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.people;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

public interface GroupService {
    public GroupFinder find(Expansion ... var1);

    public Validator validator();

    public Group createGroup(String var1);

    public void deleteGroup(String var1);

    public static interface Validator {
        public ValidationResult validateView();

        public ValidationResult validateCreate(String var1);

        public ValidationResult validateDelete(String var1);
    }

    public static interface GroupFinder
    extends SingleFetcher<Group>,
    ManyFetcher<Group> {
        public SingleFetcher<Group> withName(String var1);

        public GroupFinder withMember(User var1);
    }
}

