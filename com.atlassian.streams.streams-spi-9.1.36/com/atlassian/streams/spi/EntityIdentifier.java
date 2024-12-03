/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.spi;

import com.google.common.base.Preconditions;
import java.net.URI;

public class EntityIdentifier {
    private final URI type;
    private final String value;
    private final URI uri;

    public EntityIdentifier(URI type, String value, URI uri) {
        this.type = (URI)Preconditions.checkNotNull((Object)type, (Object)"type");
        this.uri = (URI)Preconditions.checkNotNull((Object)uri, (Object)"uri");
        this.value = (String)Preconditions.checkNotNull((Object)value, (Object)"value");
    }

    public URI getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public URI getUri() {
        return this.uri;
    }

    public boolean equals(Object other) {
        if (other instanceof EntityIdentifier) {
            EntityIdentifier ei = (EntityIdentifier)other;
            return this.type.equals(ei.type) && this.value.equals(ei.value) && this.uri.equals(ei.uri);
        }
        return false;
    }

    public int hashCode() {
        return (this.type.hashCode() * 37 + this.value.hashCode()) * 37 + this.uri.hashCode();
    }
}

