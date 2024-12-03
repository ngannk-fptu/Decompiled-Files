/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.UriTemplate;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;

public final class Link {
    private final Option<String> type;
    private final Either<UriTemplate, URI> templateOrUri;

    public Link(Either<UriTemplate, URI> templateOrUri, Option<String> type) {
        this.templateOrUri = templateOrUri;
        this.type = type;
    }

    public static Link fromUri(URI uri, Option<String> type) {
        return new Link((Either<UriTemplate, URI>)Either.right((Object)uri), type);
    }

    public static Link fromUriTemplate(UriTemplate ut, Option<String> type) {
        return new Link((Either<UriTemplate, URI>)Either.left((Object)ut), type);
    }

    public URI getUri() {
        return (URI)this.templateOrUri.fold(t -> t.resolve((Map<String, String>)ImmutableMap.of()), Function.identity());
    }

    public Option<UriTemplate> getUriTemplate() {
        return this.templateOrUri.left().toOption();
    }

    public Either<UriTemplate, URI> getTemplateOrUri() {
        return this.templateOrUri;
    }

    public Option<String> getType() {
        return this.type;
    }

    public boolean matchType(Option<String> desiredType) {
        Iterator iterator = desiredType.iterator();
        if (iterator.hasNext()) {
            String dt = (String)iterator.next();
            return this.type == null ? dt.equals("application/json") : dt.equals(this.type);
        }
        return true;
    }

    public String stringValue() {
        Iterator iterator = this.getUriTemplate().iterator();
        if (iterator.hasNext()) {
            UriTemplate ut = (UriTemplate)iterator.next();
            return ut.getValue();
        }
        return this.getUri().toASCIIString();
    }

    public boolean equals(Object other) {
        if (other instanceof Link) {
            Link o = (Link)other;
            return this.templateOrUri.equals(o.templateOrUri) && ObjectUtils.equals(this.type, o.type);
        }
        return false;
    }

    public int hashCode() {
        return this.templateOrUri.hashCode() + (this.type == null ? 0 : this.type.hashCode());
    }
}

