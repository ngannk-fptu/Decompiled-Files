/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.api.service.people.PersonService$SinglePersonFetcher
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.impl.service.finder.people;

import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.impl.service.finder.NoopFetcher;
import com.atlassian.sal.api.user.UserKey;

public class NoopPersonFinder
extends NoopFetcher<Person>
implements PersonService.PersonFinder {
    public PersonService.PersonFinder withUserKey(UserKey userKey) {
        return this;
    }

    public PersonService.SinglePersonFetcher withUsername(String username) {
        return this;
    }

    public PersonService.PersonFinder withMembershipOf(Group group) {
        return this;
    }
}

