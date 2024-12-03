/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.spring.container.ContainerManager;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class LazyLoadingFragmentTransformer
implements FragmentTransformer {
    private volatile FragmentTransformer delegate;
    private final String delegateId;

    public LazyLoadingFragmentTransformer(String delegateId) {
        if (StringUtils.isBlank((CharSequence)delegateId)) {
            throw new IllegalArgumentException("A delegate bean id must be supplied.");
        }
        this.delegateId = delegateId;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.getDelegate().handles(startElementEvent, conversionContext);
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        return this.getDelegate().transform(reader, mainFragmentTransformer, conversionContext);
    }

    private FragmentTransformer getDelegate() {
        if (this.delegate == null) {
            this.delegate = (FragmentTransformer)ContainerManager.getComponent((String)this.delegateId);
        }
        return this.delegate;
    }
}

