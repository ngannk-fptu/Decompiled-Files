/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.util.Optional;

abstract class DelegatingLink
implements Link {
    private final Link delegate;

    public DelegatingLink(Link delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate cannot be null.");
        }
        this.delegate = delegate;
    }

    public Link getDelegate() {
        return this.delegate;
    }

    @Override
    public LinkBody<?> getBody() {
        return this.delegate.getBody();
    }

    @Override
    public String getTooltip() {
        return this.delegate.getTooltip();
    }

    @Override
    public ResourceIdentifier getDestinationResourceIdentifier() {
        return this.delegate.getDestinationResourceIdentifier();
    }

    @Override
    public Link updateDestination(ResourceIdentifier destination) {
        return this.delegate.updateDestination(destination);
    }

    @Override
    public String getAnchor() {
        return this.delegate.getAnchor();
    }

    @Override
    public Optional<String> getTarget() {
        return this.delegate.getTarget();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DelegatingLink that = (DelegatingLink)o;
        return this.delegate.equals(that.delegate);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return "DelegatingLink: " + this.delegate.toString();
    }
}

