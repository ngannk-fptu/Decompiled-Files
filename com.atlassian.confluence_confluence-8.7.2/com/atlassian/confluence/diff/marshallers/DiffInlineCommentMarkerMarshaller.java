/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff.marshallers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarker;

public class DiffInlineCommentMarkerMarshaller
implements Marshaller<InlineCommentMarker> {
    @Override
    public Streamable marshal(InlineCommentMarker inlineCommentMarker, ConversionContext conversionContext) throws XhtmlException {
        return inlineCommentMarker.getBodyStream();
    }
}

