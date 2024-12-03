/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.util.Optional;

public interface Link {
    public ResourceIdentifier getDestinationResourceIdentifier();

    public LinkBody<?> getBody();

    public String getTooltip();

    public String getAnchor();

    public Optional<String> getTarget();

    public Link updateDestination(ResourceIdentifier var1);
}

