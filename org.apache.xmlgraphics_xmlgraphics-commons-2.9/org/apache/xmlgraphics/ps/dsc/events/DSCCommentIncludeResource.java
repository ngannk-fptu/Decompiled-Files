/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.ps.dsc.events.AbstractResourceDSCComment;

public class DSCCommentIncludeResource
extends AbstractResourceDSCComment {
    public DSCCommentIncludeResource() {
    }

    public DSCCommentIncludeResource(PSResource resource) {
        super(resource);
    }

    @Override
    public String getName() {
        return "IncludeResource";
    }
}

