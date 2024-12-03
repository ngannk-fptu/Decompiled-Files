/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.beans.factory.annotation.Qualifier;

public class InlineCommentMarkerHelper {
    private final XmlOutputFactory xmlOutputFactory;

    public InlineCommentMarkerHelper(@Qualifier(value="xmlOutputFactory") XmlOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    public String generateMarkerRef() {
        return UUID.randomUUID().toString();
    }

    public String toStorageFormat(String markerRef) throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter((Writer)stringWriter);
        xmlStreamWriter.writeEmptyElement(InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG.getPrefix(), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG.getLocalPart(), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG.getNamespaceURI());
        xmlStreamWriter.writeAttribute(InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR.getPrefix(), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR.getNamespaceURI(), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR.getLocalPart(), markerRef);
        xmlStreamWriter.close();
        return stringWriter.toString();
    }
}

