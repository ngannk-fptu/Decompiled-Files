/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype;

import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ModuleContentTypeStrategyDecorator
implements ContentTypeStrategy {
    private static final Map<String, String> DEFAULT_CONTENT_TYPES = ImmutableMap.builder().put((Object)"js", (Object)"application/javascript").put((Object)"css", (Object)"text/css").put((Object)"svg", (Object)"image/svg+xml").put((Object)"svgz", (Object)"image/svg+xml").put((Object)"ttf", (Object)"application/x-font-truetype").put((Object)"woff", (Object)"application/font-woff").put((Object)"woff2", (Object)"application/font-woff").put((Object)"otf", (Object)"application/x-font-opentype").put((Object)"eot", (Object)"application/vnd.ms-fontobject").build();
    private ContentTypeStrategy contentTypeStrategy;
    private String type;

    ModuleContentTypeStrategyDecorator(ContentTypeStrategy contentTypeStrategy, String type) {
        this.contentTypeStrategy = contentTypeStrategy;
        this.type = type;
    }

    @Override
    public String getContentType() {
        switch (this.type) {
            case "css": {
                return "text/css";
            }
            case "js": {
                return "application/javascript";
            }
        }
        String fromResource = this.contentTypeStrategy.getContentType();
        return StringUtils.isBlank((CharSequence)fromResource) ? DEFAULT_CONTENT_TYPES.getOrDefault(this.type, fromResource) : fromResource;
    }
}

