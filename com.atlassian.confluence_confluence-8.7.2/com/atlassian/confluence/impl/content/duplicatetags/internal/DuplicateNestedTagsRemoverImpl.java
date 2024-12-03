/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.content.duplicatetags.DuplicateNestedTagsRemover;
import com.atlassian.confluence.impl.content.duplicatetags.internal.SingleRootTreeData;
import com.atlassian.confluence.impl.content.duplicatetags.internal.SingleXmlBranchDuplicateAnalyser;
import com.atlassian.confluence.impl.content.duplicatetags.internal.SingleXmlBranchReader;
import com.atlassian.confluence.impl.content.duplicatetags.internal.XmlEventToNodeConverter;
import com.google.common.annotations.VisibleForTesting;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateNestedTagsRemoverImpl
implements DuplicateNestedTagsRemover {
    private static final Logger log = LoggerFactory.getLogger(DuplicateNestedTagsRemoverImpl.class);
    private static final boolean DEV_MODE = ConfluenceSystemProperties.isDevMode();
    private static final Integer MAX_GROUP_SIZE_OF_REPETITIVE_TAGS = Integer.getInteger("confluence.duplicate-tag-removal.max-group-size-of-repetitive-tags", 3);
    private static final Integer MIN_NESTED_LEVEL_TO_REMOVE = Integer.getInteger("confluence.duplicate-tag-removal.min-nested-level", 4);
    private static final String TAGS_ALLOWED_FOR_REMOVAL_PROPERTY_NAME = "confluence.duplicate-tag-removal.allowed-tags";
    private static final Set<String> DEFAULT_TAGS_ALLOWED_FOR_REMOVAL = Set.of("div", "span");
    private static final boolean DISABLE_DUPLICATE_TAGS_REMOVAL = Boolean.getBoolean("confluence.duplicate-tag-removal.disable");
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final SingleXmlBranchReader singleXmlBranchReader;
    private final SingleXmlBranchDuplicateAnalyser singleXmlBranchDuplicateAnalyser;
    private final boolean disableAlgorithm;

    public DuplicateNestedTagsRemoverImpl(XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory) {
        this(xmlFragmentOutputFactory, xmlEventReaderFactory, new SingleXmlBranchReader(new XmlEventToNodeConverter()), DuplicateNestedTagsRemoverImpl.createDefaultSingleXmlTreeAnalyser(), DISABLE_DUPLICATE_TAGS_REMOVAL);
    }

    @VisibleForTesting
    public DuplicateNestedTagsRemoverImpl(XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, SingleXmlBranchReader singleXmlBranchReader, SingleXmlBranchDuplicateAnalyser singleXmlBranchDuplicateAnalyser, boolean disableAlgorithm) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.singleXmlBranchReader = singleXmlBranchReader;
        this.singleXmlBranchDuplicateAnalyser = singleXmlBranchDuplicateAnalyser;
        this.disableAlgorithm = disableAlgorithm;
    }

    private static SingleXmlBranchDuplicateAnalyser createDefaultSingleXmlTreeAnalyser() {
        String tagsAllowedForRemovalAsString = System.getProperty(TAGS_ALLOWED_FOR_REMOVAL_PROPERTY_NAME);
        return new SingleXmlBranchDuplicateAnalyser(DuplicateNestedTagsRemoverImpl.getAllowedTags(tagsAllowedForRemovalAsString), MAX_GROUP_SIZE_OF_REPETITIVE_TAGS, MIN_NESTED_LEVEL_TO_REMOVE);
    }

    @VisibleForTesting
    static Set<String> getAllowedTags(String allowedTagsAsString) {
        if (allowedTagsAsString == null) {
            return DEFAULT_TAGS_ALLOWED_FOR_REMOVAL;
        }
        Set<String> allowedTags = Arrays.stream(allowedTagsAsString.split(",")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        if (allowedTags.isEmpty()) {
            throw new IllegalArgumentException("Allowed tag list for the duplicate tag removal is empty. Check the validity of the system variable confluence.duplicate-tag-removal.allowed-tags");
        }
        return allowedTags;
    }

    @Override
    public String cleanQuietly(String inputXml) {
        try {
            return this.clean(inputXml);
        }
        catch (Exception e) {
            if (DEV_MODE) {
                log.warn("Unable to parse XML document. Unable to check whether the document has duplicate nested tags or not. Turn debug logging on for the full stack trace, Error: {}", (Object)e.getMessage());
            }
            log.debug("Unable to parse XML document. Unable to check whether the document has duplicate nested tags or not.", (Throwable)e);
            return inputXml;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String clean(String inputXml) throws XMLStreamException {
        if (this.disableAlgorithm) {
            return inputXml;
        }
        StringWriter cleanedXmlContent = new StringWriter();
        XMLEventReader xmlEventReader = null;
        int numberOfRemovedDuplicateEvents = 0;
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(inputXml));
            while (xmlEventReader.hasNext()) {
                numberOfRemovedDuplicateEvents += this.processNextTopLevelBranch(xmlEventReader, cleanedXmlContent);
            }
        }
        catch (Throwable throwable) {
            StaxUtils.closeQuietly(xmlEventReader);
            throw throwable;
        }
        StaxUtils.closeQuietly(xmlEventReader);
        if (numberOfRemovedDuplicateEvents == 0) {
            return inputXml;
        }
        return cleanedXmlContent.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int processNextTopLevelBranch(XMLEventReader xmlEventReader, StringWriter cleanedXmlContent) throws XMLStreamException {
        int n;
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(cleanedXmlContent);
            n = this.processNextSingleRootTree(xmlEventReader, xmlEventWriter);
        }
        catch (Throwable throwable) {
            StaxUtils.closeQuietly(xmlEventWriter);
            throw throwable;
        }
        StaxUtils.closeQuietly(xmlEventWriter);
        return n;
    }

    private int processNextSingleRootTree(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        SingleRootTreeData singleRootTreeData = this.singleXmlBranchReader.readCurrentTopLevelBranchFromTheInputStream(xmlEventReader);
        List<XMLEvent> finalListOfEvents = this.singleXmlBranchDuplicateAnalyser.getAllNotDuplicateTags(singleRootTreeData);
        for (XMLEvent xmlEvent : finalListOfEvents) {
            xmlEventWriter.add(xmlEvent);
        }
        return singleRootTreeData.getAllXmlEvents().size() - finalListOfEvents.size();
    }
}

