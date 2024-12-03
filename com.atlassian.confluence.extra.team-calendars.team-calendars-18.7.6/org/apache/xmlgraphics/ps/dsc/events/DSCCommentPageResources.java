/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.util.Collection;
import org.apache.xmlgraphics.ps.dsc.events.AbstractResourcesDSCComment;

public class DSCCommentPageResources
extends AbstractResourcesDSCComment {
    public DSCCommentPageResources() {
    }

    public DSCCommentPageResources(Collection resources) {
        super(resources);
    }

    @Override
    public String getName() {
        return "PageResources";
    }
}

