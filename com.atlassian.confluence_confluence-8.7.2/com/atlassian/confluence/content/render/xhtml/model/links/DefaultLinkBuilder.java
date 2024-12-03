/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.util.Objects;
import java.util.Optional;

public class DefaultLinkBuilder {
    private Optional<ResourceIdentifier> destinationResourceIdentifier = Optional.empty();
    private Optional<LinkBody<?>> body = Optional.empty();
    private Optional<String> tooltip = Optional.empty();
    private Optional<String> anchor = Optional.empty();
    private Optional<String> target = Optional.empty();

    DefaultLinkBuilder() {
    }

    DefaultLinkBuilder(Link link) {
        Objects.requireNonNull(link);
        this.destinationResourceIdentifier = Optional.ofNullable(link.getDestinationResourceIdentifier());
        this.body = Optional.ofNullable(link.getBody());
        this.tooltip = Optional.ofNullable(link.getTooltip());
        this.anchor = Optional.ofNullable(link.getAnchor());
        this.target = link.getTarget();
    }

    public DefaultLink build() {
        return new DefaultLink(this);
    }

    public DefaultLinkBuilder withDestinationResourceIdentifier(Optional<ResourceIdentifier> destinationResourceIdentifier) {
        this.destinationResourceIdentifier = Objects.requireNonNull(destinationResourceIdentifier);
        return this;
    }

    public DefaultLinkBuilder withDestinationResourceIdentifier(ResourceIdentifier destinationResourceIdentifier) {
        this.destinationResourceIdentifier = Optional.ofNullable(destinationResourceIdentifier);
        return this;
    }

    public DefaultLinkBuilder withBody(Optional<LinkBody<?>> body) {
        this.body = Objects.requireNonNull(body);
        return this;
    }

    public DefaultLinkBuilder withBody(LinkBody<?> body) {
        this.body = Optional.ofNullable(body);
        return this;
    }

    public DefaultLinkBuilder withTooltip(Optional<String> tooltip) {
        this.tooltip = Objects.requireNonNull(tooltip);
        return this;
    }

    public DefaultLinkBuilder withTooltip(String tooltip) {
        this.tooltip = Optional.ofNullable(tooltip);
        return this;
    }

    public DefaultLinkBuilder withAnchor(Optional<String> anchor) {
        this.anchor = Objects.requireNonNull(anchor);
        return this;
    }

    public DefaultLinkBuilder withAnchor(String anchor) {
        this.anchor = Optional.ofNullable(anchor);
        return this;
    }

    public DefaultLinkBuilder withTarget(Optional<String> target) {
        this.target = Objects.requireNonNull(target);
        return this;
    }

    public DefaultLinkBuilder withTarget(String target) {
        this.target = Optional.ofNullable(target);
        return this;
    }

    public Optional<ResourceIdentifier> getDestinationResourceIdentifier() {
        return this.destinationResourceIdentifier;
    }

    public Optional<LinkBody<?>> getBody() {
        return this.body;
    }

    public Optional<String> getTooltip() {
        return this.tooltip;
    }

    public Optional<String> getAnchor() {
        return this.anchor;
    }

    public Optional<String> getTarget() {
        return this.target;
    }
}

