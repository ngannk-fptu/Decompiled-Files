/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.TextFieldDescriptor
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.JsonField;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TextFieldTransformation
implements Function<JsonField, Option<FieldDescriptor>> {
    private static final Logger log = LoggerFactory.getLogger(TextFieldTransformation.class);

    TextFieldTransformation() {
    }

    public Option<FieldDescriptor> apply(@NonNull JsonField input) {
        if (input.getNodeValue().isTextual()) {
            return Option.some((Object)new TextFieldDescriptor(input.getFieldName(), input.getNodeValue().getTextValue(), FieldDescriptor.Store.NO));
        }
        log.debug("Couldn't transform JSON node to a text field type. Problematic node: {}", (Object)input.getNodeValue());
        return Option.none();
    }
}

