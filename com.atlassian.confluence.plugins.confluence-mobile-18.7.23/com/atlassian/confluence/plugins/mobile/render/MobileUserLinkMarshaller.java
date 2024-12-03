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
import com.atlassian.confluence.plugins.mobile.AnonymousUserSupport;
import com.atlassian.confluence.xhtml.api.Link;

public class MobileUserLinkMarshaller
implements Marshaller<Link> {
    private final AnonymousUserSupport anonymousUserSupport;
    private final Marshaller<Link> permittedUserLinkMarshaller;
    private final Marshaller<Link> nonPermittedUserLinkMarshaller;

    public MobileUserLinkMarshaller(AnonymousUserSupport anonymousUserSupport, Marshaller<Link> permittedUserLinkMarshaller, Marshaller<Link> nonPermittedUserLinkMarshaller) {
        this.anonymousUserSupport = anonymousUserSupport;
        this.permittedUserLinkMarshaller = permittedUserLinkMarshaller;
        this.nonPermittedUserLinkMarshaller = nonPermittedUserLinkMarshaller;
    }

    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        if (this.anonymousUserSupport.isProfileViewPermitted()) {
            return this.permittedUserLinkMarshaller.marshal((Object)link, conversionContext);
        }
        return this.nonPermittedUserLinkMarshaller.marshal((Object)link, conversionContext);
    }
}

