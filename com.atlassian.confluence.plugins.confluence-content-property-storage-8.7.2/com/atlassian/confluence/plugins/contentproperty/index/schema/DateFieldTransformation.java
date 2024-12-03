/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.JsonNode
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.JsonField;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DateFieldTransformation
implements Function<JsonField, Option<FieldDescriptor>> {
    private static final Logger log = LoggerFactory.getLogger(DateFieldTransformation.class);
    private static final DateTimeFormatter ISO8601_DATETIME_PARSER = ISODateTimeFormat.dateTimeParser();

    DateFieldTransformation() {
    }

    public Option<FieldDescriptor> apply(@NonNull JsonField input) {
        JsonNode jsonNode = input.getNodeValue();
        if (jsonNode.isLong() && jsonNode.getLongValue() > 0L) {
            return this.createFromLong(input.getFieldName(), jsonNode.getLongValue());
        }
        if (jsonNode.isTextual()) {
            return this.createFromString(input.getFieldName(), jsonNode.getTextValue());
        }
        log.debug("Couldn't transform JSON node to a date field type. Problematic node: {}", (Object)input.getNodeValue());
        return Option.none();
    }

    private Option<FieldDescriptor> createFromDate(String fieldName, Date value) {
        return Option.some((Object)new StringFieldDescriptor(fieldName, LuceneUtils.dateToString((Date)value), FieldDescriptor.Store.NO));
    }

    private Option<FieldDescriptor> createFromLong(String fieldName, long value) {
        return this.createFromDate(fieldName, new DateTime(value).toDate());
    }

    private Option<FieldDescriptor> createFromString(String fieldName, String value) {
        try {
            return this.createFromDate(fieldName, ISO8601_DATETIME_PARSER.parseDateTime(value).toDate());
        }
        catch (IllegalArgumentException e) {
            log.debug("Exception during date parsing occurred, {} did not match any registered datetime formats", (Object)value);
            return Option.none();
        }
    }
}

