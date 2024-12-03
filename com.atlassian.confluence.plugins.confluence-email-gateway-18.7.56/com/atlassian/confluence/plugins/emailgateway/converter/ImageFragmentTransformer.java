/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.xhtml.api.EmbeddedImage
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class ImageFragmentTransformer
implements FragmentTransformer {
    private final Marshaller<EmbeddedImage> storageMarshaller;
    private final Unmarshaller<EmbeddedImage> emailUnmarshaller;

    public ImageFragmentTransformer(MarshallingRegistry marshallingRegistry) {
        this.storageMarshaller = marshallingRegistry.getMarshaller(EmbeddedImage.class, MarshallingType.STORAGE);
        this.emailUnmarshaller = marshallingRegistry.getUnmarshaller(EmbeddedImage.class, MarshallingType.EMAIL);
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.emailUnmarshaller.handles(startElementEvent, conversionContext);
    }

    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        EmbeddedImage image = (EmbeddedImage)this.emailUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
        return this.storageMarshaller.marshal((Object)image, conversionContext);
    }
}

