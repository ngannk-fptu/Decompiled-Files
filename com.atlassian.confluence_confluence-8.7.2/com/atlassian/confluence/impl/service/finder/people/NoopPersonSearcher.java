/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.people.PersonService$PersonSearcher
 */
package com.atlassian.confluence.impl.service.finder.people;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.impl.service.finder.NoopFetcher;

public class NoopPersonSearcher
extends NoopFetcher<Person>
implements PersonService.PersonSearcher {
    public PersonService.PersonSearcher forUnsyncedUsers(String username) {
        return this;
    }
}

