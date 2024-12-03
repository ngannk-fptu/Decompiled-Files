/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.PluggableTransformerChain;
import com.atlassian.confluence.content.render.xhtml.TransformerChain;
import com.atlassian.confluence.content.render.xhtml.TransformerWeight;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.editor.EditorXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.storage.StorageXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.InlineBodyMacroFixingTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.RemoveApostropheEntityTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.AvailableToPlugins;
import java.util.Arrays;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TransformerContextConfig {
    @Resource
    private PluginAccessor pluginAccessor;
    @Resource
    private XmlOutputFactory xmlFragmentOutputFactory;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private XMLEventFactoryProvider xmlEventFactoryProvider;
    @Resource
    private FragmentTransformer storageToViewFragmentTransformer;
    @Resource
    private FragmentTransformer storageToEditorFragmentTransformer;
    @Resource
    private FragmentTransformer editorToStorageFragmentTransformer;

    TransformerContextConfig() {
    }

    @Bean
    Transformer storageToViewTransformer() {
        return new PluggableTransformerChain(this.pluginAccessor, Arrays.asList(new TransformerWeight(new TransformerChain(Arrays.asList(new InlineBodyMacroFixingTransformer(this.xmlFragmentOutputFactory, this.xmlEventReaderFactory, this.xmlEventFactoryProvider), new StorageXhtmlTransformer(this.xmlEventReaderFactory, this.storageToViewFragmentTransformer), this.removeApostropheEntityTransformer())), 0)), "storageToView");
    }

    @Bean
    Transformer storageToEditorTransformer() {
        return new PluggableTransformerChain(this.pluginAccessor, Arrays.asList(new TransformerWeight(new TransformerChain(Arrays.asList(new StorageXhtmlTransformer(this.xmlEventReaderFactory, this.storageToEditorFragmentTransformer), this.removeApostropheEntityTransformer())), 0)), "storageToEditor");
    }

    @Bean
    Transformer editorToStorageTransformer() {
        return new PluggableTransformerChain(this.pluginAccessor, Arrays.asList(new TransformerWeight(new TransformerChain(Arrays.asList(new EditorXhtmlTransformer(this.xmlEventReaderFactory, this.editorToStorageFragmentTransformer))), 0)), "editorToStorage");
    }

    @Bean
    @AvailableToPlugins
    Transformer removeApostropheEntityTransformer() {
        return new RemoveApostropheEntityTransformer();
    }
}

