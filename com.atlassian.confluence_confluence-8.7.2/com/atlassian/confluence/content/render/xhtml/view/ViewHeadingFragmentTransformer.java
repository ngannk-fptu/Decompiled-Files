/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.ContentExcerptUtils;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.TextExtractingXmlFragmentEventReader;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.LegacyFragmentTransformer;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.renderer.PageContext;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class ViewHeadingFragmentTransformer
extends LegacyFragmentTransformer
implements FragmentTransformer {
    private static final Set<QName> HEADING_ELEMENTS = Set.of(new QName("http://www.w3.org/1999/xhtml", "h1"), new QName("http://www.w3.org/1999/xhtml", "h2"), new QName("http://www.w3.org/1999/xhtml", "h3"), new QName("http://www.w3.org/1999/xhtml", "h4"), new QName("http://www.w3.org/1999/xhtml", "h5"), new QName("http://www.w3.org/1999/xhtml", "h6"));
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public ViewHeadingFragmentTransformer(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return HEADING_ELEMENTS.contains(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String transformToString(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StringBuilder result = new StringBuilder("");
        try {
            String justText;
            String text;
            StartElement startElement = reader.peek().asStartElement();
            String tag = startElement.getName().getLocalPart();
            Iterator<Attribute> attributes = startElement.getAttributes();
            try (TextExtractingXmlFragmentEventReader fragmentReader = new TextExtractingXmlFragmentEventReader(this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader));){
                text = Streamables.writeToString(mainFragmentTransformer.transform(fragmentReader, mainFragmentTransformer, conversionContext));
                justText = fragmentReader.getText();
            }
            if (StringUtils.isNotBlank((CharSequence)text)) {
                PageContext pageContext;
                PageContext pageContext2 = pageContext = conversionContext == null ? null : conversionContext.getPageContext();
                if (pageContext == null) {
                    pageContext = new PageContext();
                }
                if (StringUtils.isBlank((CharSequence)justText)) {
                    justText = ContentExcerptUtils.extractTextSummaryFromXhtmlContent(text, 300, 300);
                }
                String escapedAnchorText = AbstractPageLink.generateUniqueAnchor(pageContext, justText);
                result.append("<");
                result.append(tag);
                String idTag = " id=\"" + escapedAnchorText + "\"";
                while (attributes.hasNext()) {
                    Attribute attr = attributes.next();
                    String attrName = attr.getName().toString();
                    if (attrName.equals("id")) {
                        idTag = " " + attrName + "=\"" + attr.getValue() + "\"";
                        continue;
                    }
                    result.append(" ").append(attrName).append("=\"").append(attr.getValue()).append("\"");
                }
                result.append(idTag);
                result.append(">");
                result.append(text);
                result.append("</");
                result.append(tag);
                result.append(">");
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
        return result.toString();
    }
}

