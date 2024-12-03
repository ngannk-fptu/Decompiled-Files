/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.inlinecommentmarker;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarker;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class StorageInlineCommentMarkerMarshaller
implements Marshaller<InlineCommentMarker> {
    private final XMLOutputFactory xmlOutputFactory;

    public StorageInlineCommentMarkerMarshaller(XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(InlineCommentMarker inlineCommentMarker, ConversionContext conversionContext) throws XhtmlException {
        if (StringUtils.isEmpty((CharSequence)inlineCommentMarker.getRef())) {
            return inlineCommentMarker.getBodyStream();
        }
        return out -> {
            XMLStreamWriter xmlStreamWriter = null;
            try {
                xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                StaxUtils.writeStartElement(xmlStreamWriter, InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG);
                StaxUtils.writeAttribute(xmlStreamWriter, InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR, inlineCommentMarker.getRef());
                StaxUtils.writeRawXML(xmlStreamWriter, out, inlineCommentMarker.getBodyStream());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.flush();
            }
            catch (XMLStreamException e) {
                try {
                    throw new IOException(e);
                }
                catch (Throwable throwable) {
                    StaxUtils.closeQuietly(xmlStreamWriter);
                    throw throwable;
                }
            }
            StaxUtils.closeQuietly(xmlStreamWriter);
        };
    }
}

