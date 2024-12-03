/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.link;

import com.atlassian.confluence.api.model.BaseApiEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;

public final class LinkType
extends BaseApiEnum {
    public static final LinkType WEB_UI = new LinkType("webui");
    public static final LinkType TINY_UI = new LinkType("tinyui");
    public static final LinkType DOWNLOAD = new LinkType("download");
    public static final LinkType EDIT_UI = new LinkType("edit");
    public static final LinkType THUMBNAIL = new LinkType("thumbnail");
    public static final List<LinkType> BUILT_IN = Collections.unmodifiableList(Arrays.asList(WEB_UI, TINY_UI, DOWNLOAD, EDIT_UI, THUMBNAIL));

    public LinkType(String type) {
        super(type);
    }

    @JsonCreator
    public static LinkType valueOf(String type) {
        for (LinkType contentType : BUILT_IN) {
            if (!type.equals(contentType.getType())) continue;
            return contentType;
        }
        return new LinkType(type);
    }

    public String getType() {
        return this.getValue();
    }
}

