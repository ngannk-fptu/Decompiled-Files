/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;

public interface UnidentifiedAttachmentMarshaller {
    public Streamable marshalPlaceholder(XmlStreamWriterTemplate var1, EmbeddedImage var2, AttachmentResourceIdentifier var3, ConversionContext var4) throws XhtmlException;
}

