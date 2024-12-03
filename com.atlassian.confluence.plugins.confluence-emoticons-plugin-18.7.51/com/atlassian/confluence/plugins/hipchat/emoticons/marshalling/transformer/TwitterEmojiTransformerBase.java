/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.plugins.hipchat.emoticons.TwitterEmoji;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.EmoticonTransformerBase;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;

public class TwitterEmojiTransformerBase
extends EmoticonTransformerBase {
    private final MarshallingType marshallingType;

    @VisibleForTesting
    protected MarshallingType getMarshallingType() {
        return this.marshallingType;
    }

    TwitterEmojiTransformerBase(MarshallingType marshallingType, MarshallingType unmarshallingType, XmlOutputFactoryProvider xmlOutputFactoryProvider, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher, @ComponentImport MarshallingRegistry marshallingRegistry) {
        super(unmarshallingType, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, Lists.newArrayList((Object[])new MarshallingFragmentTransformer[]{new MarshallingFragmentTransformer(TwitterEmoji.class, marshallingType, unmarshallingType, marshallingRegistry)}));
        this.marshallingType = marshallingType;
    }
}

