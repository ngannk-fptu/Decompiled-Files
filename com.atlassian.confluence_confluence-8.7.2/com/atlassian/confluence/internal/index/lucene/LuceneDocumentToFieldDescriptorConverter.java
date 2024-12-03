/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.document.DoubleField
 *  org.apache.lucene.document.FloatField
 *  org.apache.lucene.document.IntField
 *  org.apache.lucene.document.LongField
 *  org.apache.lucene.document.StoredField
 *  org.apache.lucene.document.StringField
 *  org.apache.lucene.document.TextField
 *  org.apache.lucene.index.IndexableField
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.plugins.index.api.DoubleFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FloatFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.IntFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.LongFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StoredFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public final class LuceneDocumentToFieldDescriptorConverter {
    public static Collection<FieldDescriptor> convertDocument(Document document) {
        ArrayList<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        for (IndexableField indexableField : document) {
            FieldDescriptor fieldDescriptor;
            FieldDescriptor.Store store;
            FieldDescriptor.Store store2 = store = indexableField.fieldType().stored() ? FieldDescriptor.Store.YES : FieldDescriptor.Store.NO;
            if (indexableField instanceof StringField) {
                fieldDescriptor = new StringFieldDescriptor(indexableField.name(), indexableField.stringValue(), store);
            } else if (indexableField instanceof TextField) {
                fieldDescriptor = new TextFieldDescriptor(indexableField.name(), indexableField.stringValue(), store);
            } else if (indexableField instanceof FloatField) {
                fieldDescriptor = new FloatFieldDescriptor(indexableField.name(), indexableField.numericValue().floatValue(), store);
            } else if (indexableField instanceof DoubleField) {
                fieldDescriptor = new DoubleFieldDescriptor(indexableField.name(), indexableField.numericValue().doubleValue(), store);
            } else if (indexableField instanceof IntField) {
                fieldDescriptor = new IntFieldDescriptor(indexableField.name(), indexableField.numericValue().intValue(), store);
            } else if (indexableField instanceof LongField) {
                fieldDescriptor = new LongFieldDescriptor(indexableField.name(), indexableField.numericValue().longValue(), store);
            } else if (indexableField instanceof StoredField) {
                fieldDescriptor = new StoredFieldDescriptor(indexableField.name(), indexableField.stringValue());
            } else if (indexableField.fieldType().indexed()) {
                FieldDescriptor.Index index = indexableField.fieldType().tokenized() ? FieldDescriptor.Index.ANALYZED : FieldDescriptor.Index.NOT_ANALYZED;
                fieldDescriptor = new FieldDescriptor(indexableField.name(), indexableField.stringValue(), store, index);
            } else {
                fieldDescriptor = new StoredFieldDescriptor(indexableField.name(), indexableField.stringValue());
            }
            fields.add(fieldDescriptor);
        }
        return fields;
    }
}

