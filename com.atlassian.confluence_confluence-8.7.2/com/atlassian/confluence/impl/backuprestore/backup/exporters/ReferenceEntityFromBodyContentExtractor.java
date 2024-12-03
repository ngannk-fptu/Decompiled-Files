/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.StorageXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.PluginFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ReferencesFromBodyContentExtractorMarkerV2;
import com.atlassian.confluence.content.render.xhtml.transformers.ThrowExceptionOnFragmentTransformationError;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.RenderContext;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReferenceEntityFromBodyContentExtractor {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    private final PluginAccessor pluginAccessor;
    private final List<Supplier<FragmentTransformer>> defaultSupplyTransformerList;

    public ReferenceEntityFromBodyContentExtractor(XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory, PluginAccessor pluginAccessor, List<Supplier<FragmentTransformer>> defaultSupplyFragmentTransformers) {
        List<Supplier<FragmentTransformer>> tmpSupplyFragmentTransformers = defaultSupplyFragmentTransformers;
        if (tmpSupplyFragmentTransformers == null) {
            tmpSupplyFragmentTransformers = Collections.emptyList();
        }
        this.defaultSupplyTransformerList = tmpSupplyFragmentTransformers;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.pluginAccessor = pluginAccessor;
    }

    Collection<EntityObjectReadyForExport.Reference> extractReferenceContentFrom(String bodyContent) throws XhtmlException {
        ArrayList<EntityObjectReadyForExport.Reference> references = new ArrayList<EntityObjectReadyForExport.Reference>();
        List<FragmentTransformer> defaultTransformerList = this.defaultSupplyTransformerList.stream().map(supplier -> (FragmentTransformer)supplier.get()).collect(Collectors.toList());
        PluginFragmentTransformer<ReferencesFromBodyContentExtractorMarkerV2> pluginFragmentTransformer = new PluginFragmentTransformer<ReferencesFromBodyContentExtractorMarkerV2>(this.pluginAccessor, "storageToView", ReferencesFromBodyContentExtractorMarkerV2.class, defaultTransformerList);
        DefaultFragmentTransformer fragmentTransformer = new DefaultFragmentTransformer(Collections.singletonList(pluginFragmentTransformer), this.xmlOutputFactory, this.xmlEventReaderFactory, new ThrowExceptionOnFragmentTransformationError(), null);
        StorageXhtmlTransformer storageXhtmlTransformer = new StorageXhtmlTransformer(this.xmlEventReaderFactory, fragmentTransformer);
        storageXhtmlTransformer.transform(new StringReader(bodyContent), new DefaultConversionContext(new RenderContext()));
        pluginFragmentTransformer.getFragmentTransformers().stream().map(ReferencesFromBodyContentExtractorMarkerV2.class::cast).flatMap(referencesFromBodyContentExtractorMarker -> referencesFromBodyContentExtractorMarker.getReferences().stream()).map(exportingReference -> new EntityObjectReadyForExport.Reference(exportingReference.getPropertyName(), exportingReference.getReferencedClazz(), new EntityObjectReadyForExport.Property("id", exportingReference.getReferencedId()))).forEach(reference -> references.add((EntityObjectReadyForExport.Reference)reference));
        return references;
    }
}

