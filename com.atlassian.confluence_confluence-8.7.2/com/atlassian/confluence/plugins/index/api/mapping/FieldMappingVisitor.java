/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.mapping.BinaryFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.BooleanFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.DateFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.DoubleFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FloatFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.IntFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping;

public interface FieldMappingVisitor<T> {
    public T visit(StringFieldMapping var1);

    public T visit(TextFieldMapping var1);

    public T visit(IntFieldMapping var1);

    public T visit(LongFieldMapping var1);

    public T visit(FloatFieldMapping var1);

    public T visit(DoubleFieldMapping var1);

    public T visit(DateFieldMapping var1);

    public T visit(BinaryFieldMapping var1);

    public T visit(BooleanFieldMapping var1);

    public T visit(NestedStringFieldMapping var1);
}

