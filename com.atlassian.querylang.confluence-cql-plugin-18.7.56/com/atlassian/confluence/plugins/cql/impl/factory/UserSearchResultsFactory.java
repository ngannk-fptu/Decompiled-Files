/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.plugins.cql.impl.factory.ModelResultFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSearchResultsFactory
implements ModelResultFactory<User> {
    private static final Logger log = LoggerFactory.getLogger(UserSearchResultsFactory.class);
    private final Set<String> requiredIndexFields = ImmutableSet.of((Object)SearchFieldNames.USER_KEY, (Object)SearchFieldNames.USER_NAME, (Object)SearchFieldNames.USER_FULLNAME, (Object)SearchFieldNames.PROFILE_PICTURE_URL);
    private static final int ICON_WIDTH = 48;
    private static final int ICON_HEIGHT = 48;
    private final PersonService personService;
    private final WebResourceUrlProvider webResourceUrlProvider;

    @Autowired
    public UserSearchResultsFactory(@ComponentImport PersonService personService, @ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        this.personService = personService;
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public Map<SearchResult, User> buildFrom(Iterable<SearchResult> searchResults, Expansions expansions) {
        HashMap results = Maps.newHashMap();
        for (SearchResult result : searchResults) {
            this.checkIsUser(result);
            if (expansions.isEmpty()) {
                results.put(result, new KnownUser(this.getIcon(result), result.getField(SearchFieldNames.USER_NAME), result.getField(SearchFieldNames.USER_FULLNAME), result.getField(SearchFieldNames.USER_KEY)));
                continue;
            }
            String userKey = result.getField(SearchFieldNames.USER_KEY);
            if (userKey != null) {
                Optional lookedUpUser = this.personService.find(expansions.toArray()).withUserKey(new UserKey(userKey)).fetch();
                Optional<Person> user = lookedUpUser.filter(person -> person instanceof User);
                user.ifPresent(u -> results.put(result, (User)lookedUpUser.get()));
                if (user.isPresent()) continue;
                log.warn("Could not find user with key in database.  Database is possibly out of sync with lucene index. UserKey : {}, found user : {}", (Object)userKey, lookedUpUser.orElse(null));
                continue;
            }
            log.warn("Index entry for personal information with id ({}) with username ({}) does not have an indexed userkey", (Object)result.getHandle(), (Object)result.getField("username"));
        }
        return results;
    }

    private void checkIsUser(SearchResult result) {
        if (!result.getType().equals(ContentTypeEnum.PERSONAL_INFORMATION.getRepresentation())) {
            throw new IllegalArgumentException("Search result does not reference a user : " + result.getType());
        }
    }

    private Icon getIcon(SearchResult result) {
        String path = this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + result.getField(SearchFieldNames.PROFILE_PICTURE_URL);
        return new Icon(path, 48, 48, false);
    }

    @Override
    public boolean handles(ContentTypeEnum contentType) {
        return ContentTypeEnum.PERSONAL_INFORMATION.equals((Object)contentType);
    }

    @Override
    public Set<String> getRequiredIndexFields() {
        return this.requiredIndexFields;
    }
}

