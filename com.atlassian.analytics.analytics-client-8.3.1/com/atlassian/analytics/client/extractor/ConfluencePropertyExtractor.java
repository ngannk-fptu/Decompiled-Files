/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.event.events.security.SecurityEvent
 *  com.atlassian.confluence.event.events.space.SpaceCreateEvent
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.MultiTextFieldQuery
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.EntityObjectPropertyContributor;
import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.security.SecurityEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MultiTextFieldQuery;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfluencePropertyExtractor
implements PropertyExtractor {
    private static final ImmutableSet<String> EXCLUDE_PROPERTIES = ImmutableSet.of((Object)"source", (Object)"class", (Object)"timestamp", (Object)"token");
    private final PropertyExtractorHelper helper = new PropertyExtractorHelper((Set<String>)EXCLUDE_PROPERTIES, new EntityObjectPropertyContributor(), new PluginPropertyContributor());
    private final UserManager userManager;

    public ConfluencePropertyExtractor(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Map<String, Object> extractProperty(String name, Object value) {
        String username;
        Comment comment;
        ContentEntityObject container;
        SpaceContentEntityObject spaceCEO;
        if (this.isExcluded(name)) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder builder = ImmutableMap.builder();
        if (value instanceof ContentEntityObject) {
            builder.put((Object)(name + ".type"), (Object)String.valueOf(((ContentEntityObject)value).getType()));
        }
        if (value instanceof SpaceContentEntityObject && (spaceCEO = (SpaceContentEntityObject)value).getSpace() != null) {
            builder.put((Object)(name + ".space.id"), (Object)String.valueOf(spaceCEO.getSpace().getId()));
        }
        if (value instanceof Page) {
            Page page = (Page)value;
            builder.put((Object)(name + ".homePage"), (Object)String.valueOf(page.isHomePage()));
        }
        if (value instanceof Comment && (container = (comment = (Comment)value).getContainer()) != null) {
            builder.putAll(this.extractProperty(name + ".owner", container));
        }
        if (value instanceof SearchQuery) {
            builder.putAll(this.extractSearchQuery(name, (SearchQuery)value));
        }
        if (value instanceof com.atlassian.user.User && !(username = ((com.atlassian.user.User)value).getName()).equals(this.getRemoteUser())) {
            builder.put((Object)name, (Object)username);
        }
        if (value instanceof User) {
            builder.put((Object)(name + ".name"), (Object)((User)value).getName());
        }
        builder.putAll(this.helper.extractProperty(name, value));
        return builder.build();
    }

    @Override
    public boolean isExcluded(String name) {
        return this.helper.isExcluded(name);
    }

    @Override
    public String extractName(Object event) {
        return this.helper.extractName(event);
    }

    @Override
    public String extractUser(Object event, Map<String, Object> properties) {
        if (event instanceof SecurityEvent) {
            return ((SecurityEvent)event).getUsername();
        }
        return this.getRemoteUser();
    }

    @Override
    public String extractSubProduct(Object event, String product) {
        return this.helper.extractSubProduct(event, product);
    }

    @Override
    public String getApplicationAccess() {
        return "";
    }

    private String getRemoteUser() {
        UserProfile userProfile = this.userManager.getRemoteUser();
        return userProfile == null ? "" : userProfile.getUsername();
    }

    private Map<String, Object> extractSearchQuery(String name, SearchQuery query) {
        if (query.getKey().equals("boosting")) {
            try {
                Method getWrappedQuery = query.getClass().getMethod("getWrappedQuery", new Class[0]);
                query = (SearchQuery)getWrappedQuery.invoke((Object)query, new Object[0]);
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException getWrappedQuery) {
                // empty catch block
            }
        }
        if (query instanceof BooleanQuery) {
            return this.extractBooleanQuery(name, (BooleanQuery)query);
        }
        if (query instanceof MultiTextFieldQuery) {
            return this.extractMultiTextFieldQuery(name, (MultiTextFieldQuery)query);
        }
        String key = query.getKey();
        if (query.getParameters().size() == 1) {
            return this.extractProperty(name + "." + key, query.getParameters().get(0));
        }
        return this.extractProperty(name + "." + key, query.getParameters());
    }

    private Map<String, Object> extractMultiTextFieldQuery(String name, MultiTextFieldQuery query) {
        if (query.getOperator() == BooleanOperator.AND && query.getFields().size() > 5) {
            return ImmutableMap.of((Object)(name + ".siteSearch"), (Object)query.getQuery());
        }
        String key = query.getKey();
        ImmutableSortedSet fields = ImmutableSortedSet.copyOf((Collection)query.getFields());
        return ImmutableMap.of((Object)(name + "." + key + ".query"), (Object)query.getQuery(), (Object)(name + "." + key + ".fields"), (Object)fields.toString());
    }

    @VisibleForTesting
    Map<String, Object> extractBooleanQuery(String name, BooleanQuery query) {
        HashMap properties = new HashMap();
        if (query.getShouldQueries().isEmpty() && query.getMustNotQueries().isEmpty()) {
            for (SearchQuery subQuery : query.getMustQueries()) {
                Map<String, Object> toAdd = this.extractProperty(name, subQuery);
                toAdd.keySet().forEach(key -> properties.putIfAbsent(key, toAdd.get(key)));
            }
            return ImmutableMap.copyOf(properties);
        }
        Map<String, Object> must = this.extractProperty(name + ".must", query.getMustQueries());
        must.keySet().forEach(key -> properties.putIfAbsent(key, must.get(key)));
        Map<String, Object> should = this.extractProperty(name + ".should", query.getShouldQueries());
        should.keySet().forEach(key -> properties.putIfAbsent(key, should.get(key)));
        Map<String, Object> mustNot = this.extractProperty(name + ".mustNot", query.getMustNotQueries());
        mustNot.keySet().forEach(key -> properties.putIfAbsent(key, mustNot.get(key)));
        return ImmutableMap.copyOf(properties);
    }

    @Override
    public Map<String, Object> enrichProperties(Object event) {
        if (event instanceof SpaceCreateEvent) {
            SpaceCreateEvent spaceCreateEvent = (SpaceCreateEvent)event;
            return ImmutableMap.of((Object)"spaceName", (Object)spaceCreateEvent.getSpace().getName());
        }
        return Collections.emptyMap();
    }

    @Override
    public String extractRequestCorrelationId(RequestInfo request) {
        return this.helper.extractRequestCorrelationId(request);
    }
}

