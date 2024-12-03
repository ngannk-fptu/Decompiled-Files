/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.util.Collection;
import org.apache.xmlgraphics.ps.dsc.events.AbstractResourcesDSCComment;

public class DSCCommentDocumentNeededResources
extends AbstractResourcesDSCComment {
    public DSCCommentDocumentNeededResources() {
    }

    public DSCCommentDocumentNeededResources(Collection resources) {
        super(resources);
    }

    @Override
    public String getName() {
        return "DocumentNeededResources";
    }
}

