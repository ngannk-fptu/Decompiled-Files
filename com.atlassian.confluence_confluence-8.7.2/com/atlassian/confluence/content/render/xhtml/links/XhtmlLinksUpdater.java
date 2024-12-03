/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdateException;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierFactory;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class XhtmlLinksUpdater
implements LinksUpdater {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;
    private final Marshaller<ResourceIdentifier> resourceIdentifierMarshaller;
    private final ResourceIdentifierFactory resourceIdentifierFactory;
    private static final Set<QName> SUPPORTED_RESOURCES = Set.of(StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME, StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME);

    public XhtmlLinksUpdater(XmlEventReaderFactory xmlEventReaderFactory, XMLOutputFactory xmlOutputFactory, XMLEventFactory xmlEventFactory, Marshaller<ResourceIdentifier> resourceIdentifierMarshaller, ResourceIdentifierFactory resourceIdentifierFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventFactory = xmlEventFactory;
        this.resourceIdentifierMarshaller = resourceIdentifierMarshaller;
        this.resourceIdentifierFactory = resourceIdentifierFactory;
    }

    @Override
    public String updateReferencesInContent(String content, LinksUpdater.PartialReferenceDetails oldLinkDetails, LinksUpdater.PartialReferenceDetails newLinkDetails) {
        Map<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> oldToNew = Collections.emptyMap();
        if (oldLinkDetails != null && newLinkDetails != null) {
            oldToNew = Collections.singletonMap(oldLinkDetails, newLinkDetails);
        }
        return this.updateReferencesInContent(content, oldToNew);
    }

    @Override
    public String updateReferencesInContent(String content, Map<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> oldToNewLinkDetails) {
        return this.applyRewriter(content, new BulkExactReferenceReplacementRewriter(oldToNewLinkDetails));
    }

    @Override
    public String expandRelativeReferencesInContent(SpaceContentEntityObject content) {
        return this.applyRewriter(content.getBodyAsString(), new ExpandRelativeReferencesRewriter(content));
    }

    @Override
    public String expandRelativeReferencesInContent(Comment comment) {
        ContentEntityObject container = comment.getContainer();
        if (container instanceof SpaceContentEntityObject) {
            return this.applyRewriter(comment.getBodyAsString(), new ExpandRelativeReferencesRewriter((SpaceContentEntityObject)container));
        }
        return comment.getBodyAsString();
    }

    @Override
    public String updateAttachmentReferencesInContent(String content, LinksUpdater.AttachmentReferenceDetails old, LinksUpdater.AttachmentReferenceDetails updated) {
        Map<LinksUpdater.AttachmentReferenceDetails, LinksUpdater.AttachmentReferenceDetails> oldToNew = Collections.emptyMap();
        if (old != null && updated != null) {
            oldToNew = Collections.singletonMap(old, updated);
        }
        return this.updateAttachmentReferencesInContent(content, oldToNew);
    }

    @Override
    public String updateAttachmentReferencesInContent(String content, Map<LinksUpdater.AttachmentReferenceDetails, LinksUpdater.AttachmentReferenceDetails> oldToNewLinkDetails) {
        return this.applyRewriter(content, new BulkAttachmentReferenceReplacementRewriter(oldToNewLinkDetails));
    }

    @Override
    public String contractAbsoluteReferencesInContent(SpaceContentEntityObject content) {
        Date creationDate = null;
        if (content instanceof BlogPost) {
            creationDate = content.getCreationDate();
        }
        return this.applyRewriter(content.getBodyAsString(), new ContractToRelativeReferencesRewriter(((SpaceContentEntityObject)content.getLatestVersion()).getSpaceKey(), content.getTitle(), creationDate));
    }

    @Override
    public String canonicalize(String content) {
        return this.applyRewriter(content, Rewriter.NOOP);
    }

    private String applyRewriter(String content, Rewriter rewriter) {
        if (StringUtils.isBlank((CharSequence)content)) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        ResettableXmlEventReader xmlEventReader = null;
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventReader = new ResettableXmlEventReader(this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(content)));
            xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter(stringWriter);
            while (xmlEventReader.hasNext()) {
                XMLEvent currentEvent = xmlEventReader.peek();
                int eventPosition = xmlEventReader.getCurrentEventPosition();
                if (currentEvent.isStartElement() && rewriter.shouldRewrite(xmlEventReader)) {
                    xmlEventReader.restoreEventPosition(eventPosition);
                    XMLEventReader fragmentEventReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                    rewriter.rewrite(fragmentEventReader, xmlEventWriter);
                    if (!fragmentEventReader.hasNext()) continue;
                    throw new LinksUpdateException("rewriter did not consume the whole fragment passed to it.");
                }
                xmlEventReader.restoreEventPosition(eventPosition);
                xmlEventWriter.add(xmlEventReader.nextEvent());
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(xmlEventReader);
        StaxUtils.closeQuietly(xmlEventWriter);
        return stringWriter.toString();
    }

    private void rewriteAttachment(XMLEventReader reader, XMLEventWriter writer, Rewriter innerRewriter, LinksUpdater.AttachmentReferenceDetails newDetails) throws XMLStreamException {
        StartElement attachmentElement = reader.nextEvent().asStartElement();
        ArrayList<Attribute> attachmentAttributesToWrite = new ArrayList<Attribute>();
        Iterator<Attribute> attributesIterator = attachmentElement.getAttributes();
        while (attributesIterator.hasNext()) {
            Attribute attribute = attributesIterator.next();
            if (StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME.equals(attribute.getName())) {
                attachmentAttributesToWrite.add(this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME, newDetails.getAttachmentName()));
                continue;
            }
            attachmentAttributesToWrite.add(attribute);
        }
        writer.add(this.xmlEventFactory.createStartElement(attachmentElement.getName(), attachmentAttributesToWrite.iterator(), null));
        innerRewriter.rewrite(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), writer);
        writer.add(reader);
    }

    private class ContractToRelativeReferencesRewriter
    implements Rewriter {
        private final String spaceKey;
        private final String contentTitle;
        private final String creationDate;

        public ContractToRelativeReferencesRewriter(String spaceKey, String contentTitle, Date creationDate) {
            this.spaceKey = spaceKey;
            this.contentTitle = contentTitle;
            this.creationDate = creationDate != null ? XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(creationDate) : null;
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            if (!reader.hasNext() || !reader.peek().isStartElement()) {
                return false;
            }
            StartElement element = reader.nextEvent().asStartElement();
            if (this.isElementWithPageTitle(element) && this.hasColonInPageTitle(element)) {
                return false;
            }
            return this.isResourceIdentifierWithMatchingSpace(element);
        }

        private boolean isResourceIdentifierWithMatchingSpace(StartElement startElement) throws XMLStreamException {
            if (!SUPPORTED_RESOURCES.contains(startElement.getName())) {
                return false;
            }
            Attribute spaceKeyAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME);
            return spaceKeyAttr == null || this.spaceKey.equals(spaceKeyAttr.getValue());
        }

        private boolean isElementWithPageTitle(StartElement startElement) throws XMLStreamException {
            return startElement.getAttributeByName(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME) != null;
        }

        private boolean hasColonInPageTitle(StartElement startElement) throws XMLStreamException {
            return startElement.getAttributeByName(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME).getValue().contains(":");
        }

        private boolean isMatchingResourceIdentifier(StartElement startElement) throws XMLStreamException {
            Attribute creationDateAttr;
            Attribute spaceKeyAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME);
            if (spaceKeyAttr != null && !this.spaceKey.equals(spaceKeyAttr.getValue())) {
                return false;
            }
            Attribute titleAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME);
            if (titleAttr == null) {
                return false;
            }
            if (!(this.creationDate == null || (creationDateAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.POSTING_DAY_ATTRIBUTE_QNAME)) != null && this.creationDate.equals(creationDateAttr.getValue()))) {
                return false;
            }
            return this.contentTitle.equals(titleAttr.getValue());
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            if (this.isMatchingResourceIdentifier(reader.peek().asStartElement())) {
                XMLEventReader riReader = XhtmlLinksUpdater.this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
                while (riReader.hasNext()) {
                    riReader.next();
                }
            } else {
                StartElement element = reader.nextEvent().asStartElement();
                ArrayList<Attribute> attributesToWrite = new ArrayList<Attribute>();
                Iterator<Attribute> attributesIterator = element.getAttributes();
                while (attributesIterator.hasNext()) {
                    Attribute attribute = attributesIterator.next();
                    if (StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME.equals(attribute.getName())) continue;
                    attributesToWrite.add(attribute);
                }
                writer.add(XhtmlLinksUpdater.this.xmlEventFactory.createStartElement(element.getName(), attributesToWrite.iterator(), null));
            }
            writer.add(reader);
        }
    }

    private class ExactReferenceReplacementRewriter
    implements Rewriter {
        private final LinksUpdater.PartialReferenceDetails oldDetails;
        private final LinksUpdater.PartialReferenceDetails newDetails;
        private final QName oldDetailsRiType;
        private final QName newDetailsRiType;

        public ExactReferenceReplacementRewriter(LinksUpdater.PartialReferenceDetails oldDetails, LinksUpdater.PartialReferenceDetails newDetails) {
            this.oldDetails = oldDetails;
            this.newDetails = newDetails;
            this.oldDetailsRiType = this.oldDetails != null && this.oldDetails.isReferenceBlogPost() ? StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME : StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME;
            this.newDetailsRiType = this.newDetails != null && this.newDetails.isReferenceBlogPost() ? StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME : StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME;
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            if (!reader.hasNext() || !reader.peek().isStartElement()) {
                return false;
            }
            StartElement startElement = reader.peek().asStartElement();
            if (!this.oldDetailsRiType.equals(startElement.getName())) {
                return false;
            }
            Attribute spaceKeyAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME);
            if (spaceKeyAttr == null || !this.oldDetails.getSpaceKey().equals(spaceKeyAttr.getValue())) {
                return false;
            }
            Attribute titleAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME);
            if (titleAttr == null) {
                return false;
            }
            return this.oldDetails.getTitle().equals(titleAttr.getValue());
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            StartElement element = reader.nextEvent().asStartElement();
            ArrayList<Attribute> attributesToWrite = new ArrayList<Attribute>();
            Iterator<Attribute> attributesIterator = element.getAttributes();
            while (attributesIterator.hasNext()) {
                Attribute attribute = attributesIterator.next();
                if (StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME.equals(attribute.getName())) {
                    attributesToWrite.add(XhtmlLinksUpdater.this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.newDetails.getSpaceKey()));
                    continue;
                }
                if (StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME.equals(attribute.getName())) {
                    attributesToWrite.add(XhtmlLinksUpdater.this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME, this.newDetails.getTitle()));
                    continue;
                }
                if (StorageResourceIdentifierConstants.POSTING_DAY_ATTRIBUTE_QNAME.equals(attribute.getName())) continue;
                attributesToWrite.add(attribute);
            }
            if (this.newDetails.isReferenceBlogPost()) {
                attributesToWrite.add(XhtmlLinksUpdater.this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.POSTING_DAY_ATTRIBUTE_QNAME, this.newDetails.getPostingDate()));
            }
            writer.add(XhtmlLinksUpdater.this.xmlEventFactory.createStartElement(this.newDetailsRiType, attributesToWrite.iterator(), null));
            reader.nextEvent();
            writer.add(XhtmlLinksUpdater.this.xmlEventFactory.createEndElement(this.newDetailsRiType.getPrefix(), this.newDetailsRiType.getNamespaceURI(), this.newDetailsRiType.getLocalPart()));
            writer.add(reader);
        }
    }

    private class BulkExactReferenceReplacementRewriter
    extends BulkRewriter<LinksUpdater.PartialReferenceDetails> {
        public BulkExactReferenceReplacementRewriter(Map<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> oldToNewMapping) {
            super(oldToNewMapping);
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            for (Map.Entry entry : this.oldToNewMapping.entrySet()) {
                ExactReferenceReplacementRewriter rewriter = new ExactReferenceReplacementRewriter((LinksUpdater.PartialReferenceDetails)entry.getKey(), (LinksUpdater.PartialReferenceDetails)entry.getValue());
                boolean shouldRewrite = rewriter.shouldRewrite(reader);
                if (!shouldRewrite) continue;
                this.processingEntry = entry;
                this.innerRewriter = rewriter;
                return shouldRewrite;
            }
            return false;
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            this.innerRewriter.rewrite(reader, writer);
            this.innerRewriter = null;
        }
    }

    private class AttachmentReferenceReplacementRewriter
    implements Rewriter {
        private final Rewriter attachmentContainerResourceIdentifierRewriter;
        private final LinksUpdater.AttachmentReferenceDetails oldDetails;
        private final LinksUpdater.AttachmentReferenceDetails newDetails;

        public AttachmentReferenceReplacementRewriter(LinksUpdater.AttachmentReferenceDetails oldDetails, LinksUpdater.AttachmentReferenceDetails newDetails) {
            this.oldDetails = oldDetails;
            this.newDetails = newDetails;
            this.attachmentContainerResourceIdentifierRewriter = new ExactReferenceReplacementRewriter(oldDetails, newDetails);
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            if (!reader.hasNext() || !reader.peek().isStartElement()) {
                return false;
            }
            StartElement firstEvent = reader.nextEvent().asStartElement();
            if (!StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME.equals(firstEvent.getName())) {
                return false;
            }
            Attribute filenameAttribute = firstEvent.getAttributeByName(StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME);
            if (filenameAttribute == null || !this.oldDetails.getAttachmentName().equals(filenameAttribute.getValue()) || !reader.hasNext()) {
                return false;
            }
            XMLEvent secondEvent = reader.peek();
            if (!secondEvent.isStartElement()) {
                return false;
            }
            return this.attachmentContainerResourceIdentifierRewriter.shouldRewrite(reader);
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            XhtmlLinksUpdater.this.rewriteAttachment(reader, writer, this.attachmentContainerResourceIdentifierRewriter, this.newDetails);
        }
    }

    private class BulkAttachmentReferenceReplacementRewriter
    extends BulkRewriter<LinksUpdater.AttachmentReferenceDetails> {
        public BulkAttachmentReferenceReplacementRewriter(Map<LinksUpdater.AttachmentReferenceDetails, LinksUpdater.AttachmentReferenceDetails> oldToNewMapping) {
            super(oldToNewMapping);
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            if (!reader.hasNext() || !reader.peek().isStartElement()) {
                return false;
            }
            StartElement firstEvent = reader.nextEvent().asStartElement();
            if (!StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME.equals(firstEvent.getName())) {
                return false;
            }
            Attribute filenameAttribute = firstEvent.getAttributeByName(StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME);
            for (Map.Entry entry : this.oldToNewMapping.entrySet()) {
                ExactReferenceReplacementRewriter exactReferenceReplacementRewriter;
                boolean shouldRewrite;
                XMLEvent secondEvent;
                LinksUpdater.AttachmentReferenceDetails oldDetails = (LinksUpdater.AttachmentReferenceDetails)entry.getKey();
                if (filenameAttribute == null || !oldDetails.getAttachmentName().equals(filenameAttribute.getValue()) || !reader.hasNext() || !(secondEvent = reader.peek()).isStartElement() || !(shouldRewrite = (exactReferenceReplacementRewriter = new ExactReferenceReplacementRewriter((LinksUpdater.PartialReferenceDetails)entry.getKey(), (LinksUpdater.PartialReferenceDetails)entry.getValue())).shouldRewrite(reader))) continue;
                this.processingEntry = entry;
                this.innerRewriter = exactReferenceReplacementRewriter;
                return shouldRewrite;
            }
            return false;
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            XhtmlLinksUpdater.this.rewriteAttachment(reader, writer, this.innerRewriter, (LinksUpdater.AttachmentReferenceDetails)this.processingEntry.getValue());
            this.processingEntry = null;
            this.innerRewriter = null;
        }
    }

    private abstract class BulkRewriter<T extends LinksUpdater.PartialReferenceDetails>
    implements Rewriter {
        protected Rewriter innerRewriter;
        protected Map.Entry<T, T> processingEntry;
        protected Map<T, T> oldToNewMapping;

        public BulkRewriter(Map<T, T> oldToNewMapping) {
            this.oldToNewMapping = oldToNewMapping;
        }
    }

    private class ExpandRelativeReferencesRewriter
    implements Rewriter {
        private final SpaceContentEntityObject contextEntity;

        public ExpandRelativeReferencesRewriter(SpaceContentEntityObject contextEntity) {
            this.contextEntity = contextEntity;
        }

        @Override
        public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
            if (!reader.hasNext() || !reader.peek().isStartElement()) {
                return false;
            }
            StartElement firstEvent = reader.nextEvent().asStartElement();
            QName elementName = firstEvent.getName();
            if (StorageLinkConstants.LINK_ELEMENT.equals(elementName) || StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME.equals(elementName)) {
                return this.isResourceIdentifierContainerCandidateForExpansion(reader, elementName, false);
            }
            if (StorageMacroConstants.DEFAULT_PARAMETER_ELEMENT.equals(elementName) || StorageMacroConstants.MACRO_PARAMETER_ELEMENT.equals(elementName)) {
                return this.isResourceIdentifierContainerCandidateForExpansion(reader, elementName, true);
            }
            return false;
        }

        private boolean isSupportedResourceIdentifierWithoutSpaceKey(StartElement element) {
            return SUPPORTED_RESOURCES.contains(element.getName()) && element.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME) == null;
        }

        private boolean isResourceIdentifierContainerCandidateForExpansion(XMLEventReader reader, QName endName, boolean macroParameter) throws XMLStreamException {
            while (!(!reader.hasNext() || reader.peek().isEndElement() && reader.peek().asEndElement().getName().equals(endName))) {
                XMLEvent event = reader.nextEvent();
                if (!event.isStartElement()) continue;
                if (this.isSupportedResourceIdentifierWithoutSpaceKey(event.asStartElement())) {
                    return true;
                }
                if (!"http://atlassian.com/resource/identifier".equals(event.asStartElement().getName().getNamespaceURI())) continue;
                return false;
            }
            return !macroParameter;
        }

        @Override
        public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
            StartElement startElement = reader.nextEvent().asStartElement();
            writer.add(startElement);
            boolean riHandled = false;
            while (!(!reader.hasNext() || reader.peek().isEndElement() && reader.peek().asEndElement().getName().equals(startElement.getName()))) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement() && this.isSupportedResourceIdentifierWithoutSpaceKey(nextEvent.asStartElement())) {
                    StartElement riElement = nextEvent.asStartElement();
                    ArrayList<Attribute> attributesToWrite = new ArrayList<Attribute>();
                    attributesToWrite.add(XhtmlLinksUpdater.this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.contextEntity.getSpaceKey()));
                    Iterator<Attribute> attributesIterator = riElement.getAttributes();
                    while (attributesIterator.hasNext()) {
                        Attribute attribute = attributesIterator.next();
                        if (StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME.equals(attribute.getName())) continue;
                        attributesToWrite.add(attribute);
                    }
                    writer.add(XhtmlLinksUpdater.this.xmlEventFactory.createStartElement(riElement.getName(), attributesToWrite.iterator(), null));
                    riHandled = true;
                    continue;
                }
                writer.add(nextEvent);
            }
            if (!riHandled) {
                DefaultConversionContext context = new DefaultConversionContext(this.contextEntity.toPageContext());
                ResourceIdentifier ri = XhtmlLinksUpdater.this.resourceIdentifierFactory.getResourceIdentifier(this.contextEntity, context);
                try {
                    String riXml = Streamables.writeToString(XhtmlLinksUpdater.this.resourceIdentifierMarshaller.marshal(ri, context));
                    if (riXml != null) {
                        writer.add(XhtmlLinksUpdater.this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(riXml)));
                    }
                }
                catch (XhtmlException ex) {
                    throw new RuntimeException("Exception while adding a resource identifier to an attachment.", ex);
                }
            }
            writer.add(reader);
        }
    }

    private static interface Rewriter {
        public static final Rewriter NOOP = new Rewriter(){

            @Override
            public boolean shouldRewrite(XMLEventReader reader) throws XMLStreamException {
                return false;
            }

            @Override
            public void rewrite(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
                throw new IllegalStateException();
            }
        };

        public boolean shouldRewrite(XMLEventReader var1) throws XMLStreamException;

        public void rewrite(XMLEventReader var1, XMLEventWriter var2) throws XMLStreamException;
    }
}

