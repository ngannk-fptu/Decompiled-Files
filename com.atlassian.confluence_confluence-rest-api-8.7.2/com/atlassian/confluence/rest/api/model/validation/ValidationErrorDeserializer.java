/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.ObjectCodec
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.deser.std.StdDeserializer
 */
package com.atlassian.confluence.rest.api.model.validation;

import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.rest.api.model.validation.RestFieldValidationError;
import com.atlassian.confluence.rest.api.model.validation.RestValidationError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

public class ValidationErrorDeserializer
extends StdDeserializer<List<ValidationError>> {
    private static final Class<? extends ValidationError> DEFAULT_VALIDATION_CLASS = RestValidationError.class;
    private final Map<String, Class<? extends ValidationError>> registry = new HashMap<String, Class<? extends ValidationError>>();

    ValidationErrorDeserializer() {
        super(List.class);
        this.registry.put("fieldName", RestFieldValidationError.class);
    }

    public List<ValidationError> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        ObjectCodec mapper = jsonParser.getCodec();
        JsonNode root = mapper.readTree(jsonParser);
        ArrayList<ValidationError> result = new ArrayList<ValidationError>(root.size());
        for (JsonNode validationErrorJson : root) {
            Class<? extends ValidationError> validationErrorClass = DEFAULT_VALIDATION_CLASS;
            Iterator fieldNames = validationErrorJson.getFieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = (String)fieldNames.next();
                if (!this.registry.containsKey(fieldName)) continue;
                validationErrorClass = this.registry.get(fieldName);
                break;
            }
            result.add((ValidationError)mapper.readValue(validationErrorJson.traverse(), validationErrorClass));
        }
        return result;
    }
}

