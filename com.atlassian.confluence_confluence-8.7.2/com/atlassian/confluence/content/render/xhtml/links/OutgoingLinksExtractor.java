/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import java.util.Set;

public interface OutgoingLinksExtractor {
    public Set<OutgoingLink> extract(ContentEntityObject var1);
}

