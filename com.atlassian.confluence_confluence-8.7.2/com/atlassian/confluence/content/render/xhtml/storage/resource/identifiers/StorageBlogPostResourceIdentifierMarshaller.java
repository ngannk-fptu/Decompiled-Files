/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import org.apache.commons.lang3.StringUtils;

public class StorageBlogPostResourceIdentifierMarshaller
implements Marshaller<BlogPostResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageBlogPostResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(BlogPostResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        if (!resourceIdentifier.isPopulated()) {
            return Streamables.empty();
        }
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "blog-post", "http://atlassian.com/resource/identifier");
            if (StringUtils.isNotBlank((CharSequence)resourceIdentifier.getSpaceKey())) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "space-key", resourceIdentifier.getSpaceKey());
            }
            if (StringUtils.isNotBlank((CharSequence)resourceIdentifier.getTitle())) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "content-title", resourceIdentifier.getTitle());
            }
            if (resourceIdentifier.getPostingDay() != null) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "posting-day", XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(resourceIdentifier.getPostingDay().getTime()));
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

