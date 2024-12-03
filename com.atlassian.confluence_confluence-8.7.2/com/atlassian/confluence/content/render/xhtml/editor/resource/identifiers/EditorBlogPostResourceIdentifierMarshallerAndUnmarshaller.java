/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.text.ParseException;
import java.util.Calendar;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller
implements Unmarshaller<ResourceIdentifier>,
StaxStreamMarshaller<BlogPostResourceIdentifier> {
    @Override
    public void marshal(BlogPostResourceIdentifier blogPostResourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        if (StringUtils.isNotBlank((CharSequence)blogPostResourceIdentifier.getTitle())) {
            xmlStreamWriter.writeAttribute("data-content-title", blogPostResourceIdentifier.getTitle());
        }
        if (StringUtils.isNotBlank((CharSequence)blogPostResourceIdentifier.getSpaceKey())) {
            xmlStreamWriter.writeAttribute("data-space-key", blogPostResourceIdentifier.getSpaceKey());
        }
        if (blogPostResourceIdentifier.getPostingDay() != null) {
            xmlStreamWriter.writeAttribute("data-posting-day", XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(blogPostResourceIdentifier.getPostingDay().getTime()));
        }
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        Calendar postingDay;
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        String postingDayString = StaxUtils.getAttributeValue(startElement, "data-posting-day");
        try {
            postingDay = Calendar.getInstance();
            postingDay.setTime(XhtmlConstants.DATE_FORMATS.getPostingDayFormat().parse(postingDayString));
        }
        catch (ParseException e) {
            throw new XhtmlException(e);
        }
        return new BlogPostResourceIdentifier(StaxUtils.getAttributeValue(startElement, "data-space-key"), StaxUtils.getAttributeValue(startElement, "data-content-title"), postingDay);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, "data-space-key", "data-content-title", "data-posting-day");
    }
}

