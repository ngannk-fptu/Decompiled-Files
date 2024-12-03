/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapterParent
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.content.ContentEntityAdapterParent;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;

public class JsonPropertyEntityAdapter
extends ContentEntityAdapterParent {
    public static boolean isContentProperty(ContentEntityObject o) {
        return o instanceof CustomContentEntityObject && JsonPropertyEntityAdapter.isContentProperty((CustomContentEntityObject)o);
    }

    public static boolean isContentProperty(CustomContentEntityObject o) {
        return o != null && "com.atlassian.confluence.plugins.confluence-content-property-storage:content-property".equals(o.getPluginModuleKey());
    }

    public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
        return BodyType.RAW;
    }

    public boolean isAllowedContainer(ContentEntityObject child, ContentEntityObject container) {
        return !JsonPropertyEntityAdapter.isContentProperty(container) && JsonPropertyEntityAdapter.isContentProperty(child);
    }

    public boolean isIndexable(CustomContentEntityObject pluginContentEntityObject, boolean isDefaultIndexable) {
        return false;
    }
}

