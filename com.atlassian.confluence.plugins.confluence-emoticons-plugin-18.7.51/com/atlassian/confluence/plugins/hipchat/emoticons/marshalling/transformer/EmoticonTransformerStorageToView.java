/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.ComparePluginFragmentTransformerMarker
 *  com.atlassian.confluence.content.render.xhtml.transformers.ExportingReference
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.ReferencesFromBodyContentExtractorMarkerV2
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Permission
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.ComparePluginFragmentTransformerMarker;
import com.atlassian.confluence.content.render.xhtml.transformers.ExportingReference;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ReferencesFromBodyContentExtractorMarkerV2;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.HipchatEmoticonTransformerBase;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.TrackableMarshallingFragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.events.StartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class EmoticonTransformerStorageToView
extends HipchatEmoticonTransformerBase
implements ComparePluginFragmentTransformerMarker,
ReferencesFromBodyContentExtractorMarkerV2 {
    private static final Logger logger = LoggerFactory.getLogger(EmoticonTransformerStorageToView.class);
    private final XmlOutputFactoryProvider xmlOutputFactoryProvider;
    private final FragmentTransformationErrorHandler fragmentTransformationErrorHandler;
    private final EventPublisher eventPublisher;
    private final MarshallingRegistry marshallingRegistry;
    private final TransactionalHostContextAccessor transactionalHostContextAccessor;
    private final CustomEmoticonService customEmoticonService;

    public EmoticonTransformerStorageToView(@ComponentImport XmlOutputFactoryProvider xmlOutputFactoryProvider, @ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport FragmentTransformationErrorHandler fragmentTransformationErrorHandler, @ComponentImport EventPublisher eventPublisher, @ComponentImport MarshallingRegistry marshallingRegistry, @ComponentImport TransactionalHostContextAccessor transactionalHostContextAccessor, @Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService) {
        super(MarshallingType.STORAGE, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, Lists.newArrayList((Object[])new TrackableMarshallingFragmentTransformer[]{new TrackableMarshallingFragmentTransformer<HipChatEmoticon>(HipChatEmoticon.class, MarshallingType.VIEW, MarshallingType.STORAGE, marshallingRegistry)}));
        this.xmlOutputFactoryProvider = xmlOutputFactoryProvider;
        this.fragmentTransformationErrorHandler = fragmentTransformationErrorHandler;
        this.eventPublisher = eventPublisher;
        this.marshallingRegistry = marshallingRegistry;
        this.transactionalHostContextAccessor = transactionalHostContextAccessor;
        this.customEmoticonService = customEmoticonService;
    }

    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        return super.transform(input, conversionContext);
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.marshallingRegistry.getUnmarshaller(HipChatEmoticon.class, this.getUnmarshallingType()).handles(startElementEvent, conversionContext);
    }

    public Collection<ExportingReference> getReferences() {
        try {
            HashSet<ExportingReference> referenceSet = new HashSet<ExportingReference>();
            Optional<TrackableMarshallingFragmentTransformer> trackableFragmentTransformer = this.getFragmentTransformerList().stream().filter(Objects::nonNull).filter(TrackableMarshallingFragmentTransformer.class::isInstance).map(TrackableMarshallingFragmentTransformer.class::cast).filter(trackableMarshallingFragmentTransformer -> trackableMarshallingFragmentTransformer.getClazz().isAssignableFrom(HipChatEmoticon.class)).map(trackableMarshallingFragmentTransformer -> trackableMarshallingFragmentTransformer).findFirst();
            trackableFragmentTransformer.ifPresent(trackableMarshallingFragmentTransformer -> {
                Set hipChatEmoticonSet = trackableMarshallingFragmentTransformer.getUnmarshallModelSet();
                if (hipChatEmoticonSet != null && !hipChatEmoticonSet.isEmpty()) {
                    Collection shortcuts = hipChatEmoticonSet.stream().map(customEmoticon -> customEmoticon.getShortcut()).collect(Collectors.toList());
                    referenceSet.addAll(this.loadCCEOForCustomEmoji(shortcuts));
                }
            });
            return referenceSet;
        }
        catch (Exception ex) {
            logger.error("There is an exception happen during extracting reference entities", (Throwable)ex);
            return new HashSet<ExportingReference>();
        }
    }

    public FragmentTransformer createNewInstance() {
        return new EmoticonTransformerStorageToView(this.xmlOutputFactoryProvider, this.xmlEventReaderFactory, this.fragmentTransformationErrorHandler, this.eventPublisher, this.marshallingRegistry, this.transactionalHostContextAccessor, this.customEmoticonService);
    }

    private Collection<ExportingReference> loadCCEOForCustomEmoji(Collection<String> shortcuts) {
        return (Collection)this.transactionalHostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, TransactionalHostContextAccessor.Permission.READ_ONLY, () -> {
            Map<String, Long> customEmoticons = this.customEmoticonService.findIDByShortcut(shortcuts.toArray(new String[0]));
            return customEmoticons.entrySet().stream().map(entry -> new ExportingReference("emoticonRef", CustomContentEntityObject.class, entry.getValue())).collect(Collectors.toList());
        });
    }
}

