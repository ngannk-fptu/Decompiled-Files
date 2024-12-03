/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.people.PasswordChangeDetails
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UserDetailsForCreation
 *  com.atlassian.confluence.api.model.people.UserKey
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.api.service.people.PersonService$PersonSearcher
 *  com.atlassian.confluence.api.service.people.PersonService$Validator
 */
package com.atlassian.confluence.api.impl.service.people;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.people.PasswordChangeDetails;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UserDetailsForCreation;
import com.atlassian.confluence.api.model.people.UserKey;
import com.atlassian.confluence.api.service.people.PersonService;

public class ReadOnlyPersonService
implements PersonService {
    private final PersonService delegate;

    public ReadOnlyPersonService(PersonService delegate) {
        this.delegate = delegate;
    }

    public UserKey create(UserDetailsForCreation userDetailsForCreation) {
        throw new UnsupportedOperationException();
    }

    public Person getCurrentUser(Expansion ... expansions) {
        return this.delegate.getCurrentUser(expansions);
    }

    public PersonService.PersonFinder find(Expansion ... expansions) {
        return this.delegate.find(expansions);
    }

    public void disable(String username) {
        throw new UnsupportedOperationException();
    }

    public void enable(String username) {
        throw new UnsupportedOperationException();
    }

    public LongTaskSubmission delete(Person personToDelete) {
        throw new UnsupportedOperationException();
    }

    public PersonService.PersonSearcher search() {
        return this.delegate.search();
    }

    public void addMembership(String username, String groupName) {
        throw new UnsupportedOperationException();
    }

    public void removeMembership(String username, String groupName) {
        throw new UnsupportedOperationException();
    }

    public void changeUserPassword(String userName, String newPass) {
        throw new UnsupportedOperationException();
    }

    public void changeMyPassword(PasswordChangeDetails passwordChangeDetails) {
        throw new UnsupportedOperationException();
    }

    public PersonService.Validator validator() {
        return this.delegate.validator();
    }
}

