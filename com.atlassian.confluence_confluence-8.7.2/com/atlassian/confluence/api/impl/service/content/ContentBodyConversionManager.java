/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.fugue.Pair;

public interface ContentBodyConversionManager {
    public Pair<String, Reference<WebResourceDependencies>> convert(ContentRepresentation var1, String var2, ContentRepresentation var3, ContentEntityObject var4, Expansion ... var5);
}

