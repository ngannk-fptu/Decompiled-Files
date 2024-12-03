/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;

public interface ResourceIdentifierMatcher {
    public boolean matches(ContentEntityObject var1, ResourceIdentifier var2);
}

