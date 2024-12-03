/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

public class VersionCommentExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        ContentEntityObject content;
        String versionComment;
        StringBuilder stringBuilder = new StringBuilder();
        if (searchable instanceof ContentEntityObject && StringUtils.isNotEmpty((CharSequence)(versionComment = (content = (ContentEntityObject)searchable).getVersionComment()))) {
            stringBuilder.append(versionComment);
        }
        return stringBuilder;
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        ArrayList<FieldDescriptor> descriptors = new ArrayList<FieldDescriptor>();
        ContentEntityObject content = (ContentEntityObject)searchable;
        descriptors.add(SearchFieldMappings.VERSION.createField(String.valueOf(content.getVersion())));
        String versionComment = content.getVersionComment();
        if (StringUtils.isNotEmpty((CharSequence)versionComment)) {
            descriptors.add(SearchFieldMappings.LAST_UPDATE_DESCRIPTION.createField(versionComment));
        }
        return descriptors;
    }
}

