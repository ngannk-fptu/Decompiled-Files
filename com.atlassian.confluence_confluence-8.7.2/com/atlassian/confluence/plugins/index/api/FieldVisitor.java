/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.DocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.DoubleFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FloatFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.IntFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.LongFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.NumericDocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.SortedDocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StoredFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;

@Deprecated
public interface FieldVisitor<T> {
    public T visit(FieldDescriptor var1);

    public T visit(StringFieldDescriptor var1);

    public T visit(TextFieldDescriptor var1);

    public T visit(IntFieldDescriptor var1);

    public T visit(LongFieldDescriptor var1);

    public T visit(FloatFieldDescriptor var1);

    public T visit(DoubleFieldDescriptor var1);

    public T visit(StoredFieldDescriptor var1);

    public T visit(DocValuesFieldDescriptor var1);

    public T visit(SortedDocValuesFieldDescriptor var1);

    public T visit(NumericDocValuesFieldDescriptor var1);
}

