/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.LinkRenderer;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import org.apache.commons.lang3.StringUtils;

public class DefaultLinkRenderer
implements LinkRenderer {
    private final Marshaller<Link> linkMarshaller;

    public DefaultLinkRenderer(Marshaller<Link> linkMarshaller) {
        this.linkMarshaller = linkMarshaller;
    }

    @Override
    public String render(ContentEntityObject content, ConversionContext conversionContext) throws XhtmlException {
        return this.render(content, null, conversionContext);
    }

    @Override
    public String render(ContentEntityObject content, String linkAlias, ConversionContext conversionContext) throws XhtmlException {
        ContentTypeEnum contentTypeEnum;
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null");
        }
        PlainTextLinkBody linkBody = null;
        if (StringUtils.isNotBlank((CharSequence)linkAlias)) {
            linkBody = new PlainTextLinkBody(linkAlias);
        }
        if ((contentTypeEnum = ContentTypeEnum.getByRepresentation(content.getType())) == null) {
            throw new IllegalArgumentException("Unsupported content type: " + content.getType());
        }
        DefaultLink link = DefaultLink.builder().withDestinationResourceIdentifier(new IdAndTypeResourceIdentifier(content.getId(), contentTypeEnum)).withBody(linkBody).build();
        return Streamables.writeToString(this.linkMarshaller.marshal(link, conversionContext));
    }
}

