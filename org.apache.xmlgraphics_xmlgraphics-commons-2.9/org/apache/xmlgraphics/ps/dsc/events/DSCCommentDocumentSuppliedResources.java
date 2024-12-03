/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.util.Collection;
import org.apache.xmlgraphics.ps.dsc.events.AbstractResourcesDSCComment;

public class DSCCommentDocumentSuppliedResources
extends AbstractResourcesDSCComment {
    public DSCCommentDocumentSuppliedResources() {
    }

    public DSCCommentDocumentSuppliedResources(Collection resources) {
        super(resources);
    }

    @Override
    public String getName() {
        return "DocumentSuppliedResources";
    }
}

