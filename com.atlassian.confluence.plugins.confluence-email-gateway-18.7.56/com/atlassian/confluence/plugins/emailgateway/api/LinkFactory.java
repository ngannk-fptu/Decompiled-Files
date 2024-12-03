/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.LinkBody
 *  com.atlassian.confluence.xhtml.api.PlainTextLinkBody
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;

@PublicApi
public class LinkFactory {
    public static Link newURLLink(String href) {
        UrlResourceIdentifier ri = new UrlResourceIdentifier(href);
        PlainTextLinkBody body = new PlainTextLinkBody("");
        return DefaultLink.builder().withDestinationResourceIdentifier((ResourceIdentifier)ri).withBody((LinkBody)body).build();
    }
}

