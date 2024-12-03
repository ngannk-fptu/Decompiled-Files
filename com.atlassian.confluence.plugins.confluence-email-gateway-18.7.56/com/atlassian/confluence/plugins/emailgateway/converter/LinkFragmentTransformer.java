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
 *  com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.emailgateway.api.LinkConverter;
import com.atlassian.confluence.plugins.emailgateway.linkconverter.LinkConverterService;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.util.concurrent.LazyReference;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class LinkFragmentTransformer
implements FragmentTransformer {
    private final LazyReference<Unmarshaller<Link>> emailLinkUnmarshaller = new LazyReference<Unmarshaller<Link>>(){

        protected Unmarshaller<Link> create() throws Exception {
            return LinkFragmentTransformer.this.marshallingRegistry.getUnmarshaller(Link.class, MarshallingType.EMAIL);
        }
    };
    private final MarshallingRegistry marshallingRegistry;
    private final LinkConverterService linkConverterService;

    public LinkFragmentTransformer(MarshallingRegistry marshallingRegistry, LinkConverterService linkConverterService) {
        this.marshallingRegistry = marshallingRegistry;
        this.linkConverterService = linkConverterService;
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.getLinkUnmarshaller().handles(startElementEvent, conversionContext);
    }

    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        Link link = (Link)this.getLinkUnmarshaller().unmarshal(reader, mainFragmentTransformer, conversionContext);
        URL linkUrl = this.getLinkURL(link);
        Class conversionClass = Link.class;
        Object converted = null;
        for (LinkConverter<?, ?> linkConverter : this.linkConverterService.getLinkConverters()) {
            converted = linkConverter.convert(linkUrl, link.getBody());
            if (converted == null) continue;
            if (converted instanceof Link && !linkConverter.isFinal()) {
                link = converted;
                linkUrl = this.getLinkURL(link);
                continue;
            }
            conversionClass = linkConverter.getConversionClass();
            break;
        }
        if (null == converted) {
            converted = link;
        }
        Marshaller storageMarshaller = this.marshallingRegistry.getMarshaller(conversionClass, MarshallingType.STORAGE);
        return storageMarshaller.marshal(converted, conversionContext);
    }

    private URL getLinkURL(Link link) {
        UrlResourceIdentifier ri = (UrlResourceIdentifier)link.getDestinationResourceIdentifier();
        String urlStr = ri.getUrl();
        try {
            return new URL(urlStr);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private Unmarshaller<Link> getLinkUnmarshaller() {
        return (Unmarshaller)this.emailLinkUnmarshaller.get();
    }
}

