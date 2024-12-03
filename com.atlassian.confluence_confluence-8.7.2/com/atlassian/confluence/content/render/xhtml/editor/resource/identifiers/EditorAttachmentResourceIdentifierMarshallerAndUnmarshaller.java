/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorPageResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller
implements Unmarshaller<ResourceIdentifier>,
StaxStreamMarshaller<AttachmentResourceIdentifier> {
    private static final Logger logger = LoggerFactory.getLogger(EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller.class);
    private static final String FILENAME_ATTRIBUTE = "data-filename";
    private final EditorPageResourceIdentifierMarshallerAndUnmarshaller pageResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller blogPostResourceIdentifierMarshallerAndUnmarshaller;

    public EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller(EditorPageResourceIdentifierMarshallerAndUnmarshaller pageResourceIdentifierMarshallerAndUnmarshaller, EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller blogPostResourceIdentifierMarshallerAndUnmarshaller) {
        this.pageResourceIdentifierMarshallerAndUnmarshaller = pageResourceIdentifierMarshallerAndUnmarshaller;
        this.blogPostResourceIdentifierMarshallerAndUnmarshaller = blogPostResourceIdentifierMarshallerAndUnmarshaller;
    }

    @Override
    public void marshal(AttachmentResourceIdentifier attachmentResourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(FILENAME_ATTRIBUTE, attachmentResourceIdentifier.getFilename());
        AttachmentContainerResourceIdentifier attachmentContainer = attachmentResourceIdentifier.getAttachmentContainerResourceIdentifier();
        logger.debug("Marshal Editor Attachment with Container Resource Identifier: {}", (Object)attachmentContainer);
        if (attachmentContainer != null) {
            if (attachmentContainer instanceof PageResourceIdentifier) {
                this.pageResourceIdentifierMarshallerAndUnmarshaller.marshal((PageResourceIdentifier)attachmentContainer, xmlStreamWriter, context);
            } else if (attachmentContainer instanceof BlogPostResourceIdentifier) {
                this.blogPostResourceIdentifierMarshallerAndUnmarshaller.marshal((BlogPostResourceIdentifier)attachmentContainer, xmlStreamWriter, context);
            }
        }
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        ResourceIdentifier attachmentContainerResourceIdentifier = null;
        if (this.blogPostResourceIdentifierMarshallerAndUnmarshaller.handles(startElement, conversionContext)) {
            attachmentContainerResourceIdentifier = this.blogPostResourceIdentifierMarshallerAndUnmarshaller.unmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
        } else if (this.pageResourceIdentifierMarshallerAndUnmarshaller.handles(startElement, conversionContext)) {
            attachmentContainerResourceIdentifier = this.pageResourceIdentifierMarshallerAndUnmarshaller.unmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
        }
        logger.debug("Unmarshal Editor Attachment with Container Resource Identifier: {}", (Object)attachmentContainerResourceIdentifier);
        return new AttachmentResourceIdentifier((AttachmentContainerResourceIdentifier)attachmentContainerResourceIdentifier, StaxUtils.getAttributeValue(startElement, FILENAME_ATTRIBUTE));
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, FILENAME_ATTRIBUTE);
    }
}

