/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.links;

import com.atlassian.confluence.content.render.xhtml.model.links.DelegatingLink;
import com.atlassian.confluence.xhtml.api.Link;

public class EmptyLink
extends DelegatingLink {
    public EmptyLink(Link delegate) {
        super(delegate);
    }
}

