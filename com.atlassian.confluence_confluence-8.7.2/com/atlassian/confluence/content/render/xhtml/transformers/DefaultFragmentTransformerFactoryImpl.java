/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

public class DefaultFragmentTransformerFactoryImpl
implements DefaultFragmentTransformerFactory {
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final List<? extends FragmentTransformer> defaultFragmentTransformers;
    private final List<ElementTransformer> defaultElementTransformers;
    private final FragmentTransformationErrorHandler fragmentTransformationErrorHandler;
    private final EventPublisher eventPublisher;

    public DefaultFragmentTransformerFactoryImpl(List<ElementTransformer> defaultElementTransformers, List<? extends FragmentTransformer> defaultFragmentTransformers, XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.defaultFragmentTransformers = defaultFragmentTransformers;
        this.defaultElementTransformers = defaultElementTransformers;
        this.fragmentTransformationErrorHandler = fragmentTransformationErrorHandler;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public DefaultFragmentTransformer createDefault() {
        return new DefaultFragmentTransformer(this.defaultElementTransformers, this.defaultFragmentTransformers, this.xmlFragmentOutputFactory, this.xmlEventReaderFactory, this.fragmentTransformationErrorHandler, this.eventPublisher);
    }

    @Override
    public DefaultFragmentTransformer createWithCustomFragmentTransformers(List<? extends FragmentTransformer> additional) {
        if (CollectionUtils.isEmpty(additional)) {
            return this.createDefault();
        }
        ArrayList<FragmentTransformer> allTransformers = new ArrayList<FragmentTransformer>(additional.size() + this.defaultFragmentTransformers.size());
        allTransformers.addAll(additional);
        allTransformers.addAll(this.defaultFragmentTransformers);
        return new DefaultFragmentTransformer(this.defaultElementTransformers, allTransformers, this.xmlFragmentOutputFactory, this.xmlEventReaderFactory, this.fragmentTransformationErrorHandler, this.eventPublisher);
    }
}

