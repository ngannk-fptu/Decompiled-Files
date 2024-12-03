/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.BaseLink
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.renderer.links.BaseLink;
import com.atlassian.renderer.links.GenericLinkParser;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractContentEntityLink
extends BaseLink {
    protected String entityName;

    public AbstractContentEntityLink(GenericLinkParser parser) {
        super(parser);
        this.relativeUrl = true;
    }

    public abstract @Nullable ContentEntityObject getDestinationContent();

    @EnsuresNonNullIf(expression={"getDestinationContent()"}, result=true)
    public boolean hasDestination() {
        return this.getDestinationContent() != null;
    }

    public String getLinkBody() {
        if (this.isNoLinkBodyProvided()) {
            return this.makeLinkBodyFromContent();
        }
        return super.getLinkBody();
    }

    protected boolean isNoLinkBodyProvided() {
        return this.getOriginalParser().getLinkBody() == null && this.getDestinationContent() != null && super.getLinkBody().equalsIgnoreCase(this.getOriginalParser().getNotLinkBody());
    }

    private String makeLinkBodyFromContent() {
        Object title = Objects.requireNonNull(this.getDestinationContent()).getDisplayTitle();
        if (this.getOriginalParser().getAnchor() != null) {
            title = (String)title + "#" + this.getOriginalParser().getAnchor();
        }
        return title;
    }
}

