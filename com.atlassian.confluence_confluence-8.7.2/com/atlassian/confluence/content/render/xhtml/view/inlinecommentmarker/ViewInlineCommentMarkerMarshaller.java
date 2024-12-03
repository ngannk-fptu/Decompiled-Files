/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinecommentmarker;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarker;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewInlineCommentMarkerMarshaller
implements Marshaller<InlineCommentMarker> {
    private final XMLOutputFactory xmlOutputFactory;

    public ViewInlineCommentMarkerMarshaller(XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(InlineCommentMarker inlineCommentMarker, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            XMLStreamWriter xmlStreamWriter = null;
            try {
                xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                StaxUtils.writeStartElement(xmlStreamWriter, InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_HTML_TAG);
                StaxUtils.writeAttribute(xmlStreamWriter, new QName("class"), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_CLASS);
                StaxUtils.writeAttribute(xmlStreamWriter, InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_HTML_REF_ATTR, inlineCommentMarker.getRef());
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

