/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.api.services.RestNavigation
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.api.services.RestNavigation;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AbstractLinkEnricher {
    protected static final String SELF_LINK = "self";
    protected static final String ORIGINAL_LINKS_PROPERTY = "links";
    protected final RestNavigationService navigationService;
    protected final GraphQL graphql;

    protected AbstractLinkEnricher(RestNavigationService navigationService, GraphQL graphql) {
        this.navigationService = navigationService;
        this.graphql = graphql;
    }

    public RestNavigation navigation() {
        return this.navigationService.createNavigation();
    }

    protected @NonNull Map<String, Type> getEnrichedPropertyTypes(String ... linkTypes) {
        if (linkTypes.length == 0) {
            return Collections.emptyMap();
        }
        HashMap<String, Class<String>> fieldTypes = new HashMap<String, Class<String>>();
        for (String linkType : linkTypes) {
            fieldTypes.put(linkType, String.class);
        }
        return ImmutableMap.of((Object)ORIGINAL_LINKS_PROPERTY, (Object)this.graphql.createDynamicType(AbstractLinkEnricher.getTypeName(linkTypes), fieldTypes));
    }

    private static String getTypeName(String ... linkTypes) {
        return "Links" + String.join((CharSequence)"", (CharSequence[])Arrays.stream(linkTypes).map(AbstractLinkEnricher::capitalizeFirstLetter).toArray(String[]::new));
    }

    protected void enrichLinks(RestObject entity, SchemaType schemaType) {
        Map links = (Map)entity.removeProperty(ORIGINAL_LINKS_PROPERTY);
        if (links == null || links.isEmpty()) {
            return;
        }
        for (Object key : links.keySet()) {
            if (!(key instanceof LinkType)) continue;
            LinkType linkType = (LinkType)key;
            this.enrichWithLink(entity, linkType.getType(), ((Link)links.get(linkType)).getPath(), schemaType);
        }
    }

    protected void enrichWithLink(RestObject restObject, String linkKey, String linkUrl, SchemaType schemaType) {
        String linksPropertyKey = schemaType == SchemaType.GRAPHQL ? ORIGINAL_LINKS_PROPERTY : "_links";
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        Object links = restObject.getProperty(linksPropertyKey);
        if (links instanceof Map) {
            map.putAll((Map)links);
        }
        if (linkUrl != null) {
            map.put(linkKey, linkUrl);
        }
        restObject.putProperty(linksPropertyKey, (Object)ImmutableMap.copyOf(map));
    }

    private static String capitalizeFirstLetter(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}

