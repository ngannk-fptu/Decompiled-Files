/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.AtlassianDocument;

public interface AtlassianDocumentBuilder<T> {
    public AtlassianDocument build(T var1);
}

