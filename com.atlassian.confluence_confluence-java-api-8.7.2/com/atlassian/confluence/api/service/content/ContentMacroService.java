/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.MacroInstance;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

@ExperimentalApi
public interface ContentMacroService {
    public MacroInstanceFinder findInContent(ContentId var1, Expansion ... var2);

    public static interface MacroInstanceFinder
    extends SingleFetcher<MacroInstance> {
        public MacroInstanceFinder withMacroId(String var1);

        @Deprecated
        public MacroInstanceFinder withHash(String var1);

        public MacroInstanceFinder withContentVersion(int var1);
    }
}

