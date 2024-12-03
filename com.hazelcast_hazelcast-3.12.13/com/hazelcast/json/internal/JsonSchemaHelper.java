/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.com.fasterxml.jackson.core.JsonToken;
import com.hazelcast.com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.hazelcast.com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.json.JsonReducedValueParser;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.internal.json.ParseException;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.json.internal.JsonPattern;
import com.hazelcast.json.internal.JsonSchemaNameValue;
import com.hazelcast.json.internal.JsonSchemaNode;
import com.hazelcast.json.internal.JsonSchemaStructNode;
import com.hazelcast.json.internal.JsonSchemaTerminalNode;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import java.io.IOException;

public final class JsonSchemaHelper {
    private JsonSchemaHelper() {
    }

    public static JsonPattern createPattern(NavigableJsonInputAdapter input, JsonSchemaNode schemaNode, JsonPathCursor path) {
        if (schemaNode.isTerminal()) {
            return null;
        }
        JsonPattern pattern = new JsonPattern();
        while (path.getNext() != null) {
            int suggestedIndexInPath;
            if (schemaNode.isTerminal()) {
                return null;
            }
            int numberOfChildren = ((JsonSchemaStructNode)schemaNode).getChildCount();
            if (path.isArray()) {
                if (path.isAny()) {
                    pattern.addAny();
                    return pattern;
                }
                suggestedIndexInPath = path.getArrayIndex();
            } else {
                for (suggestedIndexInPath = 0; suggestedIndexInPath < numberOfChildren; ++suggestedIndexInPath) {
                    JsonSchemaNameValue nameValue = ((JsonSchemaStructNode)schemaNode).getChild(suggestedIndexInPath);
                    int nameAddress = nameValue.getNameStart();
                    if (nameAddress < 0) {
                        return null;
                    }
                    input.position(nameAddress);
                    if (input.isAttributeName(path)) break;
                }
            }
            if (JsonSchemaHelper.isValidIndex(suggestedIndexInPath, (JsonSchemaStructNode)schemaNode, path.isArray())) {
                schemaNode = ((JsonSchemaStructNode)schemaNode).getChild(suggestedIndexInPath).getValue();
                pattern.add(suggestedIndexInPath);
                continue;
            }
            return null;
        }
        return pattern;
    }

    public static JsonValue findValueWithPattern(NavigableJsonInputAdapter input, JsonSchemaNode schemaNode, JsonPattern expectedPattern, JsonPathCursor attributePath) throws IOException {
        for (int i = 0; i < expectedPattern.depth(); ++i) {
            if (attributePath.getNext() == null) {
                return null;
            }
            if (schemaNode.isTerminal()) {
                return null;
            }
            int expectedOrderIndex = expectedPattern.get(i);
            JsonSchemaStructNode structDescription = (JsonSchemaStructNode)schemaNode;
            if (structDescription.getChildCount() <= expectedOrderIndex) {
                return null;
            }
            JsonSchemaNameValue nameValue = structDescription.getChild(expectedOrderIndex);
            if (!JsonSchemaHelper.structMatches(input, nameValue, expectedOrderIndex, attributePath)) {
                return null;
            }
            schemaNode = nameValue.getValue();
        }
        if (attributePath.getNext() == null) {
            if (schemaNode.isTerminal()) {
                try {
                    JsonReducedValueParser valueParser = new JsonReducedValueParser();
                    int valuePos = ((JsonSchemaTerminalNode)schemaNode).getValueStartLocation();
                    return input.parseValue(valueParser, valuePos);
                }
                catch (ParseException parseException) {
                    throw new HazelcastException(parseException);
                }
            }
            return NonTerminalJsonValue.INSTANCE;
        }
        return null;
    }

    public static JsonSchemaNode createSchema(JsonParser parser) throws IOException {
        JsonSchemaNode dummy = new JsonSchemaStructNode(null);
        JsonSchemaStructNode parent = dummy;
        JsonToken currentToken = parser.nextToken();
        int nameLocation = -1;
        if (currentToken == null) {
            return null;
        }
        while (currentToken != null) {
            JsonSchemaNameValue nameValue;
            if (currentToken.isStructStart()) {
                JsonSchemaStructNode structNode = new JsonSchemaStructNode(parent);
                nameValue = new JsonSchemaNameValue(nameLocation, structNode);
                parent.addChild(nameValue);
                parent = structNode;
                nameLocation = -1;
            } else if (currentToken == JsonToken.FIELD_NAME) {
                nameLocation = (int)JsonSchemaHelper.getTokenLocation(parser);
            } else if (currentToken.isStructEnd()) {
                parent = parent.getParent();
                nameLocation = -1;
            } else {
                JsonSchemaTerminalNode terminalNode = new JsonSchemaTerminalNode(parent);
                terminalNode.setValueStartLocation((int)JsonSchemaHelper.getTokenLocation(parser));
                nameValue = new JsonSchemaNameValue(nameLocation, terminalNode);
                parent.addChild(nameValue);
                nameLocation = -1;
            }
            currentToken = parser.nextToken();
        }
        JsonSchemaNameValue nameValue = ((JsonSchemaStructNode)dummy).getChild(0);
        if (nameValue == null) {
            return null;
        }
        dummy = nameValue.getValue();
        dummy.setParent(null);
        return dummy;
    }

    private static boolean isValidIndex(int suggestedIndex, JsonSchemaStructNode structNode, boolean isArrayPath) {
        if (suggestedIndex >= structNode.getChildCount()) {
            return false;
        }
        JsonSchemaNameValue nameValue = structNode.getChild(suggestedIndex);
        return nameValue.isArrayItem() && isArrayPath || nameValue.isObjectItem() && !isArrayPath;
    }

    private static long getTokenLocation(JsonParser parser) {
        if (parser instanceof ReaderBasedJsonParser) {
            return parser.getTokenLocation().getCharOffset();
        }
        if (parser instanceof UTF8StreamJsonParser) {
            return parser.getTokenLocation().getByteOffset();
        }
        throw new HazelcastException("Provided parser does not support location: " + parser.getClass().getName());
    }

    private static boolean structMatches(NavigableJsonInputAdapter input, JsonSchemaNameValue nameValue, int attributeIndex, JsonPathCursor currentPath) {
        int currentNamePos = nameValue.getNameStart();
        if (currentPath.isArray()) {
            return currentNamePos == -1 && attributeIndex == currentPath.getArrayIndex();
        }
        if (currentNamePos == -1) {
            return false;
        }
        input.position(currentNamePos);
        return input.isAttributeName(currentPath);
    }
}

