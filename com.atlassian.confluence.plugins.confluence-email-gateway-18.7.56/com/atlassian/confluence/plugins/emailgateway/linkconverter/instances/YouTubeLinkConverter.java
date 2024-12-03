/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.LinkBody
 */
package com.atlassian.confluence.plugins.emailgateway.linkconverter.instances;

import com.atlassian.confluence.plugins.emailgateway.api.BaseLinkConverter;
import com.atlassian.confluence.plugins.emailgateway.api.LinkFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.net.URL;

public class YouTubeLinkConverter
extends BaseLinkConverter<Object> {
    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public Link convert(URL linkUrl, LinkBody<Object> linkBody) {
        if (linkUrl.getHost().matches(".*youtu\\.be.*")) {
            String vidId = linkUrl.getPath().substring(1);
            String newurl = "http://youtube.com/watch?v=" + vidId;
            return LinkFactory.newURLLink(newurl);
        }
        return null;
    }
}

