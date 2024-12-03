/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParser
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.diffs;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlFragmentEventReader;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.diffs.Merger;
import com.atlassian.confluence.util.diffs.SimpleMergeResult;
import com.google.gson.JsonParser;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageLayoutAwareMerger
implements Merger {
    private final Merger delegate;
    private XmlEventReaderFactory xmlEventReaderFactory;
    private XMLOutputFactory xmlOutputFactory;
    private static final Logger log = LoggerFactory.getLogger(PageLayoutAwareMerger.class);

    public PageLayoutAwareMerger(Merger merger, XmlEventReaderFactory xmlEventReaderFactory, XMLOutputFactory xmlOutputFactory) {
        this.delegate = merger;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public MergeResult mergeContent(String base, String left, String right) {
        try {
            String baseCell;
            XMLEventReader baseXmlReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(base));
            XMLEventReader leftXmlReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(left));
            XMLEventReader rightXmlReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(right));
            String leftLayout = this.getLayoutType(leftXmlReader);
            String rigthLyout = this.getLayoutType(rightXmlReader);
            String baseLayout = this.getLayoutType(baseXmlReader);
            if (!(leftLayout.equals(rigthLyout) && leftLayout.equals(baseLayout) && baseLayout.equals(rigthLyout))) {
                return SimpleMergeResult.FAIL_MERGE_RESULT;
            }
            if (StringUtils.isEmpty((CharSequence)baseLayout)) {
                return this.delegate.mergeContent(base, left, right);
            }
            ArrayList<MergeResult> results = new ArrayList<MergeResult>();
            while ((baseCell = this.getNextCellContent(baseXmlReader)) != null) {
                String leftCell = this.getNextCellContent(leftXmlReader);
                String rightCell = this.getNextCellContent(rightXmlReader);
                assert (leftCell != null);
                assert (rightCell != null);
                results.add(this.delegate.mergeContent(baseCell, leftCell, rightCell));
            }
            if (this.cellMergedSuccess(results)) {
                return new SimpleMergeResult(false, this.createResultingContent(this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(base)), results));
            }
            return SimpleMergeResult.FAIL_MERGE_RESULT;
        }
        catch (Exception e) {
            log.error("Error merging content", (Throwable)e);
            return SimpleMergeResult.FAIL_MERGE_RESULT;
        }
    }

    private String getLayoutType(XMLEventReader input) throws XMLStreamException {
        Attribute layoutAttr;
        XMLEvent elem;
        if (input.hasNext() && (elem = input.nextEvent()).isStartElement() && (layoutAttr = elem.asStartElement().getAttributeByName(new QName("data-atlassian-layout"))) != null) {
            JsonParser parser = new JsonParser();
            return parser.parse(layoutAttr.getValue()).getAsJsonObject().get("name").getAsString();
        }
        return "";
    }

    private boolean cellMergedSuccess(List<MergeResult> results) {
        for (MergeResult result : results) {
            if (!result.hasConflicts()) continue;
            return false;
        }
        return true;
    }

    private String getNextCellContent(XMLEventReader input) throws XMLStreamException {
        while (input.hasNext()) {
            XMLEvent elem = input.peek();
            if (this.isCellStart(elem)) {
                StringWriter resultingContent = new StringWriter();
                XMLEventWriter writer = this.xmlOutputFactory.createXMLEventWriter(resultingContent);
                writer.add(new XmlFragmentEventReader(input));
                writer.flush();
                writer.close();
                return resultingContent.toString();
            }
            input.nextEvent();
        }
        return null;
    }

    private boolean isCellStart(XMLEvent elem) {
        List<String> classes;
        Attribute styleClass;
        return elem.getEventType() == 1 && (styleClass = elem.asStartElement().getAttributeByName(new QName("class"))) != null && ((classes = Arrays.asList(styleClass.getValue().split("//s+"))).contains("footer") || classes.contains("innerCell") || classes.contains("header"));
    }

    private String createResultingContent(XMLEventReader baseXmlReader, List<MergeResult> results) throws XMLStreamException {
        StringWriter resultingContent = new StringWriter();
        XMLEventWriter writer = this.xmlOutputFactory.createXMLEventWriter(resultingContent);
        block0: for (MergeResult result : results) {
            while (baseXmlReader.hasNext()) {
                XMLEvent event = baseXmlReader.peek();
                if (this.isCellStart(event)) {
                    writer.add(this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(result.getMergedContent())));
                    XmlFragmentEventReader cellReader = new XmlFragmentEventReader(baseXmlReader);
                    while (cellReader.hasNext()) {
                        cellReader.nextEvent();
                    }
                    continue block0;
                }
                writer.add(baseXmlReader.nextEvent());
            }
        }
        while (baseXmlReader.hasNext()) {
            writer.add(baseXmlReader.nextEvent());
        }
        writer.flush();
        writer.close();
        return resultingContent.toString();
    }
}

