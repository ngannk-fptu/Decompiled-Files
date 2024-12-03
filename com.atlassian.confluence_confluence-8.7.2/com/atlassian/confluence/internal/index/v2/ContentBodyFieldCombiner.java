/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.confluence.impl.search.v2.lucene.ContentBodyMaxSizeSystemProperty;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class ContentBodyFieldCombiner {
    private static final int CONTENT_BODY_MAX_SIZE = new ContentBodyMaxSizeSystemProperty().getValue();
    private final StringBuilder combinedText = new StringBuilder();

    ContentBodyFieldCombiner() {
    }

    public boolean offerField(@Nonnull String fieldName, @Nullable CharSequence fieldValue) {
        if ("contentBody".equals(fieldName)) {
            if (fieldValue != null) {
                this.combinedText.append(' ').append(fieldValue);
            }
            return true;
        }
        return false;
    }

    public List<FieldDescriptor> getContentBodyFields() {
        ArrayList<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        String trimmed = this.getTrimmedText();
        if (trimmed.length() > 0) {
            fields.add(SearchFieldMappings.CONTENT.createField(trimmed));
            fields.add(SearchFieldMappings.EXACT_CONTENT_BODY.createField(trimmed));
            if (trimmed.length() <= CONTENT_BODY_MAX_SIZE) {
                fields.add(SearchFieldMappings.CONTENT_STORED.createField(trimmed));
            }
        }
        return fields;
    }

    private String getTrimmedText() {
        return this.combinedText.toString().trim();
    }
}

