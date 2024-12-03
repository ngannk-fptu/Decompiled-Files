/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.EmoticonTransformerBase;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import java.util.List;

abstract class HipchatEmoticonTransformerBase
extends EmoticonTransformerBase {
    private final List<? extends FragmentTransformer> fragmentTransformerList;

    HipchatEmoticonTransformerBase(MarshallingType marshallingType, MarshallingType unmarshallingType, XmlOutputFactoryProvider xmlOutputFactoryProvider, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher, @ComponentImport MarshallingRegistry marshallingRegistry) {
        this(unmarshallingType, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, Lists.newArrayList((Object[])new MarshallingFragmentTransformer[]{new MarshallingFragmentTransformer(HipChatEmoticon.class, marshallingType, unmarshallingType, marshallingRegistry)}));
    }

    HipchatEmoticonTransformerBase(MarshallingType unmarshallingType, XmlOutputFactoryProvider xmlOutputFactoryProvider, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher, List<? extends FragmentTransformer> fragmentTransformers) {
        super(unmarshallingType, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, fragmentTransformers);
        this.fragmentTransformerList = fragmentTransformers;
    }

    protected List<? extends FragmentTransformer> getFragmentTransformerList() {
        return this.fragmentTransformerList;
    }
}

