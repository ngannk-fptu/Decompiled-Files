/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.List;

public class MobileDelegatingLinkMarshaller
implements Marshaller<Link> {
    private final List<Marshaller<Link>> delegateMarshallers;

    public MobileDelegatingLinkMarshaller(List<Marshaller<Link>> delegateMarshallers) {
        this.delegateMarshallers = delegateMarshallers;
    }

    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        for (Marshaller<Link> marshaller : this.delegateMarshallers) {
            try {
                return marshaller.marshal((Object)link, conversionContext);
            }
            catch (Exception exception) {
            }
        }
        throw new UnsupportedOperationException("The link " + link + " could not be marshalled.");
    }
}

