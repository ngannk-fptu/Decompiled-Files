/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertWithElisions
 *  com.atlassian.diagnostics.Elisions
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializableWithType
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.TypeSerializer
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertWithElisions;
import com.atlassian.diagnostics.Elisions;
import com.atlassian.diagnostics.internal.rest.RestAlertTrigger;
import com.atlassian.diagnostics.internal.rest.RestElisions;
import com.atlassian.diagnostics.internal.rest.RestIssue;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;

public class RestAlert
implements JsonSerializableWithType {
    private final Alert alert;
    private final Elisions elisions;
    private final Set<String> suppressedFields;

    public RestAlert(@Nonnull Alert alert, String ... suppressedFields) {
        Objects.requireNonNull(alert, "alert");
        Objects.requireNonNull(suppressedFields, "suppressedFields");
        this.alert = alert;
        this.elisions = alert instanceof AlertWithElisions ? (Elisions)((AlertWithElisions)alert).getElisions().orElse(null) : null;
        this.suppressedFields = ImmutableSet.copyOf((Object[])suppressedFields);
    }

    public void serializeWithType(JsonGenerator generator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("id", this.alert.getId());
        generator.writeStringField("nodeName", this.alert.getNodeName());
        if (!this.suppressedFields.contains("issue")) {
            generator.writeObjectField("issue", (Object)new RestIssue(this.alert.getIssue()));
        }
        generator.writeNumberField("timestamp", this.alert.getTimestamp().toEpochMilli());
        generator.writeObjectField("trigger", (Object)new RestAlertTrigger(this.alert.getTrigger()));
        Object details = this.alert.getDetails().orElse(null);
        if (details != null) {
            generator.writeObjectFieldStart("details");
            generator.writeRaw(RestAlert.stripObjectStartAndEnd(this.alert.getIssue().getJsonMapper().toJson(details)));
            generator.writeEndObject();
        }
        if (this.elisions != null) {
            generator.writeObjectField("elisions", (Object)new RestElisions(this.elisions));
        }
        generator.writeEndObject();
    }

    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        this.serializeWithType(jsonGenerator, serializerProvider, null);
    }

    private static String stripObjectStartAndEnd(String json) {
        if (json != null && json.startsWith("{") && json.endsWith("}")) {
            return json.substring(1, json.length() - 1);
        }
        return json;
    }
}

