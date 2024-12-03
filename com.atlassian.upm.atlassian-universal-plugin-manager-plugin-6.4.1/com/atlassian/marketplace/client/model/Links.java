/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.model.Link;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public final class Links {
    public static final String REST_TYPE = "application/json";
    public static final String WEB_TYPE = "text/html";
    private final Map<String, ImmutableList<Link>> items;

    public Links(Map<String, ImmutableList<Link>> items) {
        this.items = ImmutableMap.copyOf(items);
    }

    public Map<String, ImmutableList<Link>> getItems() {
        return this.items;
    }

    URI requireUri(String rel) {
        Iterator iterator = this.getUri(rel).iterator();
        if (iterator.hasNext()) {
            URI uri = (URI)iterator.next();
            return uri;
        }
        throw new IllegalArgumentException("missing required REST link: " + rel);
    }

    public Option<Link> getLink(String rel) {
        Iterator<Link> iterator = this.getLinks(rel).iterator();
        if (iterator.hasNext()) {
            Link link = iterator.next();
            return Option.some((Object)link);
        }
        return Option.none();
    }

    public Option<Link> getLink(String rel, String contentType) {
        for (Link link : this.getLinks(rel)) {
            if (link.getType().isEmpty() && contentType.equals(REST_TYPE)) {
                return Option.some((Object)link);
            }
            for (String type : link.getType()) {
                if (!type.equalsIgnoreCase(contentType)) continue;
                return Option.some((Object)link);
            }
        }
        return Option.none();
    }

    public Iterable<Link> getLinks(String rel) {
        return (Iterable)Option.option(this.items.get(rel)).getOrElse((Object)ImmutableList.of());
    }

    public Option<URI> getUri(String rel) {
        Iterator iterator = this.getLink(rel).iterator();
        if (iterator.hasNext()) {
            Link link = (Link)iterator.next();
            return Option.some((Object)link.getUri());
        }
        return Option.none();
    }

    public Option<URI> getUri(String rel, String contentType) {
        Iterator iterator = this.getLink(rel, contentType).iterator();
        if (iterator.hasNext()) {
            Link link = (Link)iterator.next();
            return Option.some((Object)link.getUri());
        }
        return Option.none();
    }

    public Option<UriTemplate> getUriTemplate(String rel) {
        Iterator iterator = this.getLink(rel).iterator();
        if (iterator.hasNext()) {
            Link link = (Link)iterator.next();
            return link.getUriTemplate();
        }
        return Option.none();
    }

    public Option<UriTemplate> getUriTemplate(String rel, String contentType) {
        Iterator iterator = this.getLink(rel, contentType).iterator();
        if (iterator.hasNext()) {
            Link link = (Link)iterator.next();
            return link.getUriTemplate();
        }
        return Option.none();
    }
}

