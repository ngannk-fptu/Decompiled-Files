/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.xhtml.api.Link;

public interface ViewLinkSubMarshallerFactory {
    public Marshaller<Link> newLinkBodyMarshaller();

    public Marshaller<UnresolvedLink> newUnresolvedLinkMarshaller();

    public Marshaller<Link> newUnresolvedLinkBodyMarshaller();
}

