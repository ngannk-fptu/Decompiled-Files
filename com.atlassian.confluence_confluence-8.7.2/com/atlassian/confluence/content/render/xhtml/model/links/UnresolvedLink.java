/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.links.DelegatingLink;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Optional;

public class UnresolvedLink
extends DelegatingLink {
    private final Optional<String> tooltip;

    public UnresolvedLink(Link delegate) {
        this(delegate, Optional.empty());
    }

    public UnresolvedLink(Link delegate, Optional<String> tooltip) {
        super(delegate);
        this.tooltip = tooltip;
        if (delegate.getDestinationResourceIdentifier() == null) {
            throw new IllegalArgumentException("destinationResourceIdentifier cannot be null.");
        }
    }

    @Override
    public String getTooltip() {
        return this.tooltip.orElse(super.getTooltip());
    }
}

