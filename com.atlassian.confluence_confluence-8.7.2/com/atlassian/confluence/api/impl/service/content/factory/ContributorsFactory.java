/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContributorUsers
 *  com.atlassian.confluence.api.model.content.Contributors
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.reference.ModelListBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContributorUsers;
import com.atlassian.confluence.api.model.content.Contributors;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.ModelListBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.stream.Collectors;

public class ContributorsFactory
extends ModelFactory<List<ConfluenceUser>, Contributors> {
    private final PersonFactory personFactory;

    public ContributorsFactory(PersonFactory personFactory) {
        this.personFactory = personFactory;
    }

    @Override
    public Contributors buildFrom(List<ConfluenceUser> editContributors, Expansions expansions) {
        Reference publishersRef = expansions.canExpand("publishers") ? Reference.to((Object)this.buildContributorUsers(editContributors, expansions)) : Reference.collapsed(ContributorUsers.class);
        return Contributors.builder().publishers(publishersRef).build();
    }

    private ContributorUsers buildContributorUsers(List<ConfluenceUser> editContributors, Expansions expansions) {
        return ContributorUsers.builder().userKeys(editContributors.stream().map(user -> user != null ? user.getKey() : new UserKey("")).collect(Collectors.toList())).users(this.buildPersonsList(editContributors, expansions.getSubExpansions("publishers"))).build();
    }

    private List<Person> buildPersonsList(List<ConfluenceUser> users, Expansions expansions) {
        List persons;
        if (expansions.canExpand("users")) {
            Expansions usersExpansions = expansions.getSubExpansions("users");
            persons = ModelListBuilder.newExpandedInstance().putAll((Iterable)users.stream().map(user -> this.buildPerson((ConfluenceUser)user, usersExpansions)).collect(Collectors.toList())).build();
        } else {
            persons = ModelListBuilder.newInstance().build();
        }
        return persons;
    }

    private Person buildPerson(ConfluenceUser user, Expansions userExpansions) {
        return user != null ? this.personFactory.buildFrom(user, userExpansions) : this.personFactory.anonymous();
    }
}

