/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.hibernate.Hibernate
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.hibernate.Hibernate;

public class AttachmentOwnerContentTypeExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof Attachment) {
            ContentEntityObject content = ((Attachment)searchable).getContainer();
            if (content == null) {
                return resultBuilder.build();
            }
            resultBuilder.add((Object)SearchFieldMappings.CONTAINER_CONTENT_TYPE.createField(content.getType()));
            resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_CONTENT_TYPE.createField(Hibernate.getClass((Object)content).getName()));
        }
        return resultBuilder.build();
    }
}

