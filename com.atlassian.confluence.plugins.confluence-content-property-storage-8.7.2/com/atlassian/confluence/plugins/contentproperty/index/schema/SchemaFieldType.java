/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Function
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.DateFieldTransformation;
import com.atlassian.confluence.plugins.contentproperty.index.schema.JsonField;
import com.atlassian.confluence.plugins.contentproperty.index.schema.NumberFieldTransformation;
import com.atlassian.confluence.plugins.contentproperty.index.schema.StringFieldTransformation;
import com.atlassian.confluence.plugins.contentproperty.index.schema.TextFieldTransformation;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import org.codehaus.jackson.JsonNode;

public enum SchemaFieldType {
    STRING(new StringFieldTransformation()),
    TEXT(new TextFieldTransformation()),
    NUMBER(new NumberFieldTransformation()),
    DATE(new DateFieldTransformation());

    private final Function<JsonField, Option<FieldDescriptor>> transformation;

    private SchemaFieldType(Function<JsonField, Option<FieldDescriptor>> transformation) {
        this.transformation = transformation;
    }

    public Option<FieldDescriptor> createFrom(String fieldName, JsonNode jsonNode) {
        return (Option)this.transformation.apply((Object)new JsonField(fieldName, jsonNode));
    }
}

