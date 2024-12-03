/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.index.extractor;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.plugins.contentproperty.index.extractor.ContentPropertyExtractionManager;
import com.atlassian.confluence.plugins.contentproperty.index.extractor.JsonExpressionEvaluator;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContentPropertyExtractionManagerImpl
implements ContentPropertyExtractionManager {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertyExtractionManagerImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonExpressionEvaluator jsonExpressionEvaluator = new JsonExpressionEvaluator();

    @Override
    public Iterable<FieldDescriptor> extract(JsonString json, Iterable<ContentPropertySchemaField> schemaFields) {
        Option<JsonNode> jsonNode = this.parseJson(json);
        if (!jsonNode.isDefined()) {
            return ImmutableList.of();
        }
        JsonNode node = (JsonNode)jsonNode.get();
        ImmutableList.Builder results = ImmutableList.builder();
        for (ContentPropertySchemaField schemaField : schemaFields) {
            results.addAll(this.extract(node, schemaField));
        }
        return results.build();
    }

    private Option<JsonNode> parseJson(JsonString json) {
        try {
            return Option.option((Object)this.objectMapper.readTree(json.getValue()));
        }
        catch (IOException e) {
            log.warn("Could not read content property JSON document.");
            log.debug("Exception occurred during parsing JSON document.", (Throwable)e);
            return Option.none();
        }
    }

    private Iterable<FieldDescriptor> extract(JsonNode json, ContentPropertySchemaField schemaField) {
        ArrayList<FieldDescriptor> results = new ArrayList<FieldDescriptor>();
        for (JsonNode requestedNode : this.jsonExpressionEvaluator.evaluate(json, schemaField.getJsonExpression())) {
            Option<FieldDescriptor> createdField = schemaField.getFieldType().createFrom(schemaField.getFieldName(), requestedNode);
            if (createdField.isDefined()) {
                results.add((FieldDescriptor)createdField.get());
                continue;
            }
            log.warn("Could not extract indexable value (path '{}', type '{}') from content property JSON document. It's possible that content property index schema is incorrectly defined in plugin '{}', module '{}'.", new Object[]{schemaField.getJsonExpression(), schemaField.getFieldType(), schemaField.getOwningPlugin(), schemaField.getOwningModule()});
        }
        return results;
    }
}

