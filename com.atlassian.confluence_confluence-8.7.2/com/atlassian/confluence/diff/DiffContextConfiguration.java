/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.diff;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.content.render.xhtml.MarshallingFactory;
import com.atlassian.confluence.content.render.xhtml.UnmarshalMarshalFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.editor.EditorModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarker;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.storage.StorageXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ComparePluginFragmentTransformerMarker;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.PluginFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkSubMarshallerFactory;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewUnresolvedLinkMarshaller;
import com.atlassian.confluence.diff.ContextBlockMarkingDiffPostProcessor;
import com.atlassian.confluence.diff.DaisyDelegate;
import com.atlassian.confluence.diff.DaisyHtmlDiffer;
import com.atlassian.confluence.diff.DiffPostProcessor;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.diff.MacroIconInsertingPostProcessor;
import com.atlassian.confluence.diff.StripDaisyDiffDataPostProcessor;
import com.atlassian.confluence.diff.StripEmptySpansDiffPostProcessor;
import com.atlassian.confluence.diff.StripToContextDiffPostProcessor;
import com.atlassian.confluence.diff.WikiConvertingHtmlDiffer;
import com.atlassian.confluence.diff.XSLDiffPostProcessor;
import com.atlassian.confluence.diff.marshallers.DiffInlineCommentMarkerMarshaller;
import com.atlassian.confluence.diff.marshallers.DiffInlineTaskMarshaller;
import com.atlassian.confluence.diff.marshallers.DiffLinkMarshaller;
import com.atlassian.confluence.diff.marshallers.DiffMacroMarshaller;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiffContextConfiguration {
    @Resource
    private WikiToStorageConverter wikiToStorageConverter;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private DefaultFragmentTransformerFactory storageDefaultFragmentTransformerFactory;
    @Resource
    private MarshallingFactory<Link> linkMarshallingFactory;
    @Resource
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    @Resource
    private ViewLinkSubMarshallerFactory viewLinkSubMarshallerFactory;
    @Resource
    private ModelToRenderedClassMapper viewModelToRenderedClassMapper;
    @Resource
    private MacroMarshallingFactory macroMarshallingFactory;
    @Resource
    private MacroManager xhtmlMacroManager;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private XMLOutputFactory xmlOutputFactory;
    @Resource
    private Unmarshaller<InlineTaskList> storageInlineTaskUnmarshaller;
    @Resource
    private Unmarshaller<InlineCommentMarker> storageInlineCommentMarkerUnmarshaller;
    @Resource
    private MacroMetadataManager macroMetadataManager;
    @Resource
    private MacroIconManager macroIconManager;
    @Resource
    private I18NBeanFactory userI18NBeanFactory;
    @Resource
    private CacheFactory cacheManager;
    @Resource
    private LocaleManager localeManager;
    @Resource
    private PluginAccessor pluginAccessor;

    @Bean
    @AvailableToPlugins
    public Differ htmlDiffer() {
        return new WikiConvertingHtmlDiffer(this.wikiToStorageConverter, this.createDaisyDiffer());
    }

    private DaisyHtmlDiffer createDaisyDiffer() {
        return new DaisyHtmlDiffer((Transformer)new StorageXhtmlTransformer(this.xmlEventReaderFactory, this.storageDefaultFragmentTransformerFactory.createWithCustomFragmentTransformers(List.of(this.createLinkTransformer(), this.createMacroTransformer(), this.createInlineTaskTransformer(), this.createInlineCommentTransformer(), new PluginFragmentTransformer<ComparePluginFragmentTransformerMarker>(this.pluginAccessor, "storageToView", ComparePluginFragmentTransformerMarker.class)))), new DaisyDelegate(this.createDiffPostProcessors()), this.cacheManager, this.localeManager);
    }

    private UnmarshalMarshalFragmentTransformer<InlineCommentMarker> createInlineCommentTransformer() {
        return new UnmarshalMarshalFragmentTransformer<InlineCommentMarker>(this.storageInlineCommentMarkerUnmarshaller, new DiffInlineCommentMarkerMarshaller());
    }

    private UnmarshalMarshalFragmentTransformer<InlineTaskList> createInlineTaskTransformer() {
        return new UnmarshalMarshalFragmentTransformer<InlineTaskList>(this.storageInlineTaskUnmarshaller, new DiffInlineTaskMarshaller(this.xmlOutputFactory));
    }

    private UnmarshalMarshalFragmentTransformer<MacroDefinition> createMacroTransformer() {
        return new UnmarshalMarshalFragmentTransformer<MacroDefinition>(this.macroMarshallingFactory.getStorageUnmarshaller(), new DiffMacroMarshaller(this.xhtmlMacroManager, this.xmlFragmentOutputFactory));
    }

    private UnmarshalMarshalFragmentTransformer<Link> createLinkTransformer() {
        return new UnmarshalMarshalFragmentTransformer<Link>(this.linkMarshallingFactory.getStorageUnmarshaller(), new DiffLinkMarshaller(this.linkMarshallingFactory.getViewMarshaller(), new ViewUnresolvedLinkMarshaller(this.xmlStreamWriterTemplate, null, this.viewLinkSubMarshallerFactory.newUnresolvedLinkBodyMarshaller(), new EditorModelToRenderedClassMapper(this.viewModelToRenderedClassMapper))));
    }

    private List<DiffPostProcessor> createDiffPostProcessors() {
        return ImmutableList.of((Object)new MacroIconInsertingPostProcessor(this.macroMetadataManager, this.macroIconManager, this.userI18NBeanFactory), (Object)new ContextBlockMarkingDiffPostProcessor("diff-block-target", "diff-block-context"), (Object)new StripToContextDiffPostProcessor("diff-block-target", "diff-block-context"), (Object)new XSLDiffPostProcessor("com/atlassian/confluence/diff/diff-cleanup.xsl"), (Object)new StripEmptySpansDiffPostProcessor(), (Object)new StripDaisyDiffDataPostProcessor());
    }
}

