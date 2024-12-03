/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.DateFieldMapping;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.core.bean.EntityObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;

public class EntityDateExtractor
implements Extractor2 {
    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ArrayList<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        if (searchable instanceof EntityObject) {
            EntityObject eo = (EntityObject)searchable;
            this.extractDate(fields, SearchFieldMappings.CREATION_DATE, eo.getCreationDate());
            this.extractDate(fields, SearchFieldMappings.LAST_MODIFICATION_DATE, eo.getLastModificationDate());
        }
        return fields;
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return null;
    }

    private void extractDate(Collection<FieldDescriptor> fields, DateFieldMapping fieldDefinition, @Nullable Date date) {
        if (date != null) {
            fields.add(fieldDefinition.createField(date));
        }
    }
}

