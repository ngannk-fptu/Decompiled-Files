/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;

public interface ContentTransformerFactory {
    public Transformer getTransformer(MacroDefinitionUpdater var1);

    public Transformer getTransformer(MacroDefinitionReplacer var1);

    @Deprecated
    public Transformer getTransformer(MacroDefinitionHandler var1);

    public Transformer getTransformer(MacroDefinitionHandler var1, MacroDefinitionMarshallingStrategy var2);
}

