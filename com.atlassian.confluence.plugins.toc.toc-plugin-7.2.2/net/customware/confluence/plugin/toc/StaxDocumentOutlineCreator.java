/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XhtmlConstants
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 *  org.jsoup.select.Elements
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import net.customware.confluence.plugin.toc.DepthFirstDocumentOutlineBuilder;
import net.customware.confluence.plugin.toc.DocumentOutline;
import net.customware.confluence.plugin.toc.EmptyLevelTrimmingDocumentOutlineBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class StaxDocumentOutlineCreator {
    private static final Pattern HEADING_ELEMENT_PATTERN = Pattern.compile("[h|H]([1-6])");
    private static final Logger log = LoggerFactory.getLogger(StaxDocumentOutlineCreator.class);
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlFragmentOutputFactory;

    public StaxDocumentOutlineCreator(XmlEventReaderFactory xmlEventReaderFactory, @Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
    }

    public DocumentOutline getOutline(String pageContent) throws Exception {
        EmptyLevelTrimmingDocumentOutlineBuilder outlineBuilder = new EmptyLevelTrimmingDocumentOutlineBuilder();
        try {
            XMLEventReader reader = this.xmlEventReaderFactory.createXMLEventReader((Reader)new StringReader(pageContent), XhtmlConstants.STORAGE_NAMESPACES, false);
            int lastLevel = 1;
            while (reader.hasNext()) {
                StartElement element;
                Matcher matcher;
                XMLEvent event = reader.nextEvent();
                if (!event.isStartElement() || !(matcher = HEADING_ELEMENT_PATTERN.matcher((element = event.asStartElement()).getName().getLocalPart())).matches()) continue;
                int headingLevel = Integer.valueOf(matcher.group(1));
                String headingId = this.getHeadingId(element);
                Document headingText = Jsoup.parseBodyFragment((String)this.getHeadingText(reader, headingLevel), (String)"");
                headingText.outputSettings().prettyPrint(false);
                Elements headingLinks = headingText.select("a");
                for (Element headingLink : headingLinks) {
                    String anchorName;
                    if (headingId == null && StringUtils.isNotBlank((CharSequence)(anchorName = headingLink.attr("name")))) {
                        headingId = anchorName;
                    }
                    for (Node childNode : new LinkedList(headingLink.childNodes())) {
                        headingLink.before(childNode);
                    }
                    headingLink.remove();
                }
                lastLevel = this.insertInBuilderStructure(headingLevel, headingText.body().text(), headingId, outlineBuilder, lastLevel);
            }
        }
        catch (XMLStreamException ex) {
            log.error("Exception reading storage format data using an XMLEventReader", (Throwable)ex);
            throw new StaxOutlineBuilderException("Exception reading storage format data using an XMLEventReader", ex);
        }
        return outlineBuilder.getDocumentOutline();
    }

    private int insertInBuilderStructure(int level, String heading, String anchorText, DepthFirstDocumentOutlineBuilder builder, int lastLevel) {
        if (level < lastLevel) {
            for (int i = 0; i < lastLevel - level; ++i) {
                builder.previousLevel();
            }
        } else if (level > lastLevel) {
            for (int i = 0; i < level - lastLevel; ++i) {
                builder.nextLevel();
            }
        }
        builder.add(heading, anchorText, level);
        return level;
    }

    private String getHeadingId(StartElement element) throws XMLStreamException {
        Attribute headingId = element.getAttributeByName(new QName("id"));
        if (headingId != null) {
            return headingId.getValue();
        }
        return null;
    }

    private String getHeadingText(XMLEventReader reader, int headingLevel) throws Exception {
        XMLEvent event;
        Pattern endPattern = Pattern.compile("[h|H]" + headingLevel);
        StringWriter stringWriter = new StringWriter();
        XMLEventWriter eventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)stringWriter);
        while (!(!reader.hasNext() || (event = reader.nextEvent()).isEndElement() && endPattern.matcher(event.asEndElement().getName().getLocalPart()).matches())) {
            eventWriter.add(event);
        }
        StaxUtils.flushEventWriter((XMLEventWriter)eventWriter);
        return stringWriter.toString();
    }

    public class StaxOutlineBuilderException
    extends Exception {
        StaxOutlineBuilderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

