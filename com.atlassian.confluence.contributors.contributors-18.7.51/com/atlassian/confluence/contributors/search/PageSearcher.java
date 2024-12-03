/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.Doc;

@Internal
public interface PageSearcher {
    public Iterable<Doc> getDocuments(MacroParameterModel var1);
}

