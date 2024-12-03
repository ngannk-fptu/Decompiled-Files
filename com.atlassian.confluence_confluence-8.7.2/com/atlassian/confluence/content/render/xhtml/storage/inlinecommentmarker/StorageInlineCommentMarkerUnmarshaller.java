/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.inlinecommentmarker;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarker;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class StorageInlineCommentMarkerUnmarshaller
implements Unmarshaller<InlineCommentMarker> {
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public StorageInlineCommentMarkerUnmarshaller(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public InlineCommentMarker unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        InlineCommentMarker inlineCommentMarker;
        XMLEventReader bodyReader = null;
        try {
            String ref = StaxUtils.getAttributeValue(xmlEventReader.peek().asStartElement(), InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR);
            bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlEventReader);
            inlineCommentMarker = new InlineCommentMarker(ref, mainFragmentTransformer.transform(bodyReader, mainFragmentTransformer, conversionContext));
        }
        catch (XMLStreamException e) {
            try {
                throw new XhtmlException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(bodyReader);
                StaxUtils.closeQuietly(xmlEventReader);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(bodyReader);
        StaxUtils.closeQuietly(xmlEventReader);
        return inlineCommentMarker;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG.equals(startElementEvent.getName());
    }
}

