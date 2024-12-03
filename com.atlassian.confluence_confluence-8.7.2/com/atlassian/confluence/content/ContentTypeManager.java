/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.custom.CustomContentType;
import java.util.Collection;

public interface ContentTypeManager {
    public ContentType getContentType(String var1);

    public String getImplementingPluginVersion(String var1);

    public Collection<CustomContentType> getEnabledCustomContentTypes();
}

