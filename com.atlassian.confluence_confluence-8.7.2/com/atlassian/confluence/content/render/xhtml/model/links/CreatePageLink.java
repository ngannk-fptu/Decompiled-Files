/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.links.DelegatingLink;
import com.atlassian.confluence.xhtml.api.Link;

public class CreatePageLink
extends DelegatingLink {
    public CreatePageLink(Link delegate) {
        super(delegate);
        if (delegate.getDestinationResourceIdentifier() == null) {
            throw new IllegalArgumentException("destinationResourceIdentifier cannot be null.");
        }
    }
}

