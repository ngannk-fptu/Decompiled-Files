/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.spacedirectory.rest;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public enum SpaceDirectoryScope {
    ALL(ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION),
    PERSONAL(ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION),
    GLOBAL(ContentTypeEnum.SPACE_DESCRIPTION);

    private final Set<ContentTypeEnum> contentTypes = new HashSet<ContentTypeEnum>();

    private SpaceDirectoryScope(ContentTypeEnum ... contentTypes) {
        for (ContentTypeEnum contentType : contentTypes) {
            this.contentTypes.add(contentType);
        }
    }

    public Set<ContentTypeEnum> getContentTypes() {
        return this.contentTypes;
    }

    public static SpaceDirectoryScope toScope(String scopeStr) {
        if (StringUtils.isNotBlank((CharSequence)scopeStr)) {
            scopeStr = scopeStr.toUpperCase();
            if (PERSONAL.toString().equals(scopeStr)) {
                return PERSONAL;
            }
            if (GLOBAL.toString().equals(scopeStr)) {
                return GLOBAL;
            }
        }
        return ALL;
    }
}

