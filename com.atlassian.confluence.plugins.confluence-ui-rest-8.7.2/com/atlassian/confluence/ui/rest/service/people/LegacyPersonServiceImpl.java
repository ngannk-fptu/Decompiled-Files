/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.model.people.Anonymous
 *  com.atlassian.confluence.legacyapi.model.people.Person
 *  com.atlassian.confluence.legacyapi.service.people.PersonService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.service.people;

import com.atlassian.confluence.legacyapi.model.people.Anonymous;
import com.atlassian.confluence.legacyapi.model.people.Person;
import com.atlassian.confluence.legacyapi.service.people.PersonService;
import com.atlassian.confluence.ui.rest.builder.LegacyPersonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component(value="localPersonService")
public class LegacyPersonServiceImpl
implements PersonService {
    private final LegacyPersonBuilder personBuilder;

    @Autowired
    public LegacyPersonServiceImpl(LegacyPersonBuilder personBuilder) {
        this.personBuilder = personBuilder;
    }

    public Anonymous anonymous() {
        return this.personBuilder.anonymous();
    }

    public Person findByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }
        return this.personBuilder.forUsername(username);
    }
}

