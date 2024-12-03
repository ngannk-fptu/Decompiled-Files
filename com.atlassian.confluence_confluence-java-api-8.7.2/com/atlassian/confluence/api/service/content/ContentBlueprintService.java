/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;

public interface ContentBlueprintService {
    public Content publishInstance(Content var1, Expansion ... var2);

    public ContentBlueprintInstance createInstance(ContentBlueprintInstance var1, Expansion ... var2);
}

