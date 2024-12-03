/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.Person
 *  com.atlassian.gadgets.opensocial.spi.PersonService
 *  com.atlassian.gadgets.opensocial.spi.PersonServiceException
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.common.util.ImmediateFuture
 *  org.apache.shindig.social.ResponseError
 *  org.apache.shindig.social.opensocial.model.Person
 *  org.apache.shindig.social.opensocial.model.Person$Field
 *  org.apache.shindig.social.opensocial.spi.CollectionOptions
 *  org.apache.shindig.social.opensocial.spi.GroupId
 *  org.apache.shindig.social.opensocial.spi.PersonService
 *  org.apache.shindig.social.opensocial.spi.PersonService$SortOrder
 *  org.apache.shindig.social.opensocial.spi.RestfulCollection
 *  org.apache.shindig.social.opensocial.spi.SocialSpiException
 *  org.apache.shindig.social.opensocial.spi.UserId
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.opensocial.internal;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.internal.ShindigOpenSocialTypeAdapter;
import com.atlassian.gadgets.opensocial.model.Person;
import com.atlassian.gadgets.opensocial.spi.PersonServiceException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.ResponseError;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShindigPersonServiceImpl
implements PersonService {
    private static final Comparator<org.apache.shindig.social.opensocial.model.Person> NAME_COMPARATOR = (person, person1) -> {
        String name = person.getName().getFormatted();
        String name1 = person1.getName().getFormatted();
        return name.compareTo(name1);
    };
    private final com.atlassian.gadgets.opensocial.spi.PersonService personService;
    private final TransactionTemplate txTemplate;

    @Autowired
    public ShindigPersonServiceImpl(com.atlassian.gadgets.opensocial.spi.PersonService personService, TransactionTemplate txTemplate) {
        this.personService = personService;
        this.txTemplate = txTemplate;
    }

    public Future<RestfulCollection<org.apache.shindig.social.opensocial.model.Person>> getPeople(Set<UserId> userIds, GroupId groupId, CollectionOptions collectionOptions, Set<String> fields, SecurityToken token) throws SocialSpiException {
        try {
            Set people = (Set)this.txTemplate.execute(() -> ShindigOpenSocialTypeAdapter.getPeopleFromUserIds(this.personService, userIds, groupId, token));
            List shindigPeople = people.stream().map(ShindigOpenSocialTypeAdapter.personToShindigPersonFunction()).collect(Collectors.toList());
            if (collectionOptions.getSortBy().equals(Person.Field.NAME.toString())) {
                shindigPeople.sort(NAME_COMPARATOR);
            }
            if (collectionOptions.getSortOrder().equals((Object)PersonService.SortOrder.descending)) {
                Collections.reverse(shindigPeople);
            }
            int totalSize = shindigPeople.size();
            int last = collectionOptions.getFirst() + collectionOptions.getMax();
            shindigPeople = shindigPeople.subList(Math.min(collectionOptions.getFirst(), totalSize), Math.min(last, totalSize));
            return ImmediateFuture.newInstance((Object)new RestfulCollection(shindigPeople, collectionOptions.getFirst(), totalSize));
        }
        catch (PersonServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<org.apache.shindig.social.opensocial.model.Person> getPerson(UserId id, Set<String> fields, SecurityToken token) throws SocialSpiException {
        try {
            OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
            Person person = (Person)this.txTemplate.execute(() -> this.personService.getPerson(id.getUserId(token), openSocialRequestContext));
            if (person == null) {
                throw new SocialSpiException(ResponseError.BAD_REQUEST, "Person " + id.getUserId(token) + " not found");
            }
            return ImmediateFuture.newInstance((Object)ShindigOpenSocialTypeAdapter.convertPersonToShindigPerson(person));
        }
        catch (PersonServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }
}

