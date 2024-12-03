/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.xhtml.api.Link;

public interface ViewLinkMarshallerFactory {
    public Marshaller<Link> newPageLinkMarshaller();

    public Marshaller<Link> newPageLinkMarshaller(Marshaller<CreatePageLink> var1, HrefEvaluator var2, Marshaller<Link> var3);

    public Marshaller<Link> newBlogPostLinkMarshaller();

    public Marshaller<Link> newBlogPostLinkMarshaller(HrefEvaluator var1, Marshaller<Link> var2);

    public Marshaller<Link> newUserLinkMarshaller();

    public Marshaller<Link> newUserLinkMarshaller(HrefEvaluator var1, Marshaller<Link> var2);

    default public Marshaller<Link> newPageTemplateLinkMarshaller() {
        return null;
    }
}

