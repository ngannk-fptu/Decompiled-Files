/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.api.service.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.PasswordChangeDetails;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UserDetailsForCreation;
import com.atlassian.confluence.api.model.people.UserKey;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

@ExperimentalApi
public interface PersonService {
    public UserKey create(UserDetailsForCreation var1);

    public Person getCurrentUser(Expansion ... var1);

    public PersonFinder find(Expansion ... var1);

    public void disable(String var1);

    public void enable(String var1);

    public LongTaskSubmission delete(Person var1);

    public PersonSearcher search();

    public void addMembership(String var1, String var2);

    public void removeMembership(String var1, String var2);

    public void changeUserPassword(String var1, String var2);

    public void changeMyPassword(PasswordChangeDetails var1);

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateView();

        public ValidationResult validateDisable(String var1);

        public ValidationResult validateEnable(String var1);

        public ValidationResult validateDelete(Person var1);

        public ValidationResult validateAddMembership(String var1, String var2);

        public ValidationResult validateRemoveMembership(String var1, String var2);

        public ValidationResult validateUserCreate(UserDetailsForCreation var1);

        public ValidationResult validateChangePassword(String var1, String var2);

        public ValidationResult validateChangeMyPassword(PasswordChangeDetails var1);
    }

    public static interface PersonSearcher
    extends ManyFetcher<Person> {
        public PersonSearcher forUnsyncedUsers(String var1);
    }

    public static interface SinglePersonFetcher
    extends SingleFetcher<Person> {
    }

    public static interface PersonFinder
    extends SinglePersonFetcher,
    ManyFetcher<Person> {
        public PersonFinder withUserKey(com.atlassian.sal.api.user.UserKey var1);

        public SinglePersonFetcher withUsername(String var1);

        public PersonFinder withMembershipOf(Group var1);
    }
}

