/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.EmbeddedImage
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class EmailEmbeddedImageResourceUnmarshaller
implements Unmarshaller<EmbeddedImage> {
    public EmailEmbeddedImageResourceUnmarshaller(MarshallingRegistry registry) {
        registry.register((Unmarshaller)this, EmbeddedImage.class, MarshallingType.EMAIL);
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return "img".equalsIgnoreCase(startElementEvent.getName().getLocalPart());
    }

    public EmbeddedImage unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        UrlResourceIdentifier ri;
        String src = this.getImageSrc(xmlEventReader);
        PageContext renderContext = conversionContext.getPageContext();
        if (src.startsWith("cid:") && renderContext != null) {
            String cid = src.substring(src.indexOf(58) + 1);
            HashMap attachmentCidToName = (HashMap)renderContext.getParam((Object)"attachmentCidToName");
            String filename = (String)attachmentCidToName.get(cid);
            ri = new AttachmentResourceIdentifier(filename);
        } else {
            ri = new UrlResourceIdentifier(src);
        }
        return new DefaultEmbeddedImage((NamedResourceIdentifier)ri);
    }

    private String getImageSrc(XMLEventReader xmlEventReader) {
        StartElement imageStartElement;
        try {
            imageStartElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        Attribute srcAttribute = imageStartElement.getAttributeByName(new QName("src"));
        return srcAttribute.getValue();
    }
}

