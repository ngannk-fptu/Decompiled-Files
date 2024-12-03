/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.ContentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import java.io.StringReader;
import java.util.Objects;

public final class MacroDefinitionTransformerImpl
implements MacroDefinitionTransformer {
    private final ContentTransformerFactory contentTransformerFactory;

    public MacroDefinitionTransformerImpl(ContentTransformerFactory contentTransformerFactory) {
        this.contentTransformerFactory = Objects.requireNonNull(contentTransformerFactory);
    }

    @Override
    public String updateMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionUpdater updater) throws XhtmlException {
        return this.contentTransformerFactory.getTransformer(updater).transform(new StringReader(storageFragment), context);
    }

    @Override
    public String replaceMacroDefinitionsWithString(String storageFragment, ConversionContext context, MacroDefinitionReplacer replacer) throws XhtmlException {
        return this.contentTransformerFactory.getTransformer(replacer).transform(new StringReader(storageFragment), context);
    }

    @Override
    public void handleMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionHandler handler) throws XhtmlException {
        this.contentTransformerFactory.getTransformer(handler, MacroDefinitionMarshallingStrategy.DISCARD_MACRO).transform(new StringReader(storageFragment), context);
    }

    @Override
    public void handleMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionHandler handler, MacroDefinitionMarshallingStrategy strategy) throws XhtmlException {
        this.contentTransformerFactory.getTransformer(handler, strategy).transform(new StringReader(storageFragment), context);
    }
}

