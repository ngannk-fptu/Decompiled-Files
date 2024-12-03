/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentEntityObject
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import java.util.List;

public class JsonPropertyQueryFactory {
    public static ContentQuery<CustomContentEntityObject> findByContentIdAndKey(long contentId, String key) {
        return new ContentQuery("contentproperty.findByContentIdAndKey", new Object[]{contentId, key});
    }

    public static ContentQuery<CustomContentEntityObject> findAllByContentId(long contentId) {
        return new ContentQuery("contentproperty.findAllByContentId", new Object[]{contentId});
    }

    public static ContentQuery<CustomContentEntityObject> findAllByContentIdsAndKeys(List<Long> contentIds, List<String> keys) {
        return new ContentQuery("contentproperty.findAllByContentIdsAndKeys", new Object[]{contentIds, keys});
    }

    public static ContentQuery<CustomContentEntityObject> findBySpaceKeyAndKey(String spaceKey, String key) {
        return new ContentQuery("spaceproperty.findBySpaceKeyAndKey", new Object[]{spaceKey, key});
    }

    public static ContentQuery<CustomContentEntityObject> findAllBySpaceKey(String spaceKey) {
        return new ContentQuery("spaceproperty.findAllBySpaceKey", new Object[]{spaceKey});
    }
}

