/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLinkBuilder;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DefaultLink
implements Link {
    private final Optional<ResourceIdentifier> destinationResourceIdentifier;
    private final Optional<LinkBody<?>> body;
    private final Optional<String> tooltip;
    private final Optional<String> anchor;
    private final Optional<String> target;

    DefaultLink(DefaultLinkBuilder builder) {
        this.destinationResourceIdentifier = builder.getDestinationResourceIdentifier();
        this.body = builder.getBody();
        this.tooltip = builder.getTooltip();
        this.anchor = builder.getAnchor();
        this.target = builder.getTarget();
    }

    public static DefaultLinkBuilder builder() {
        return new DefaultLinkBuilder();
    }

    public static DefaultLinkBuilder builder(Link link) {
        return new DefaultLinkBuilder(link);
    }

    @Deprecated
    public DefaultLink(ResourceIdentifier destinationResourceIdentifier, LinkBody<?> body) {
        this(destinationResourceIdentifier, body, null, null);
    }

    @Deprecated
    public DefaultLink(ResourceIdentifier destinationResourceIdentifier, LinkBody<?> body, String tooltip, String anchor) {
        this.destinationResourceIdentifier = Optional.ofNullable(destinationResourceIdentifier);
        this.body = Optional.ofNullable(body);
        this.tooltip = Optional.ofNullable(tooltip);
        this.anchor = Optional.ofNullable(anchor);
        this.target = Optional.empty();
    }

    @Override
    public ResourceIdentifier getDestinationResourceIdentifier() {
        return this.destinationResourceIdentifier.orElse(null);
    }

    @Override
    public LinkBody<?> getBody() {
        return this.body.orElse(null);
    }

    @Override
    public String getTooltip() {
        return this.tooltip.orElse(null);
    }

    @Override
    public String getAnchor() {
        return this.anchor.orElse(null);
    }

    @Override
    public Optional<String> getTarget() {
        return this.target;
    }

    @Override
    public Link updateDestination(ResourceIdentifier destination) {
        return DefaultLink.builder(this).withDestinationResourceIdentifier(destination).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultLink link = (DefaultLink)o;
        return new EqualsBuilder().append(this.anchor, link.anchor).append(this.body, link.body).append(this.destinationResourceIdentifier, link.destinationResourceIdentifier).append(this.tooltip, link.tooltip).append(this.target, link.target).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(31, 5).append(this.destinationResourceIdentifier).append(this.body).append(this.tooltip).append(this.anchor).append(this.target).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("destination", this.destinationResourceIdentifier).append("body", this.body).append("tooltip", this.tooltip).append("anchor", this.anchor).append("target", this.target).toString();
    }
}

