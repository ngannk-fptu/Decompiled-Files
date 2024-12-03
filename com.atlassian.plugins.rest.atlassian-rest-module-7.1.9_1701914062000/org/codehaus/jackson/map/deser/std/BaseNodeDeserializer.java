/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class BaseNodeDeserializer<N extends JsonNode>
extends StdDeserializer<N> {
    public BaseNodeDeserializer(Class<N> nodeClass) {
        super(nodeClass);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }

    protected void _reportProblem(JsonParser jp, String msg) throws JsonMappingException {
        throw new JsonMappingException(msg, jp.getTokenLocation());
    }

    protected void _handleDuplicateField(String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws JsonProcessingException {
    }

    protected final ObjectNode deserializeObject(JsonParser jp, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException, JsonProcessingException {
        ObjectNode node = nodeFactory.objectNode();
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        while (t == JsonToken.FIELD_NAME) {
            JsonNode value;
            String fieldName = jp.getCurrentName();
            switch (jp.nextToken()) {
                case START_OBJECT: {
                    value = this.deserializeObject(jp, ctxt, nodeFactory);
                    break;
                }
                case START_ARRAY: {
                    value = this.deserializeArray(jp, ctxt, nodeFactory);
                    break;
                }
                case VALUE_STRING: {
                    value = nodeFactory.textNode(jp.getText());
                    break;
                }
                default: {
                    value = this.deserializeAny(jp, ctxt, nodeFactory);
                }
            }
            JsonNode old = node.put(fieldName, value);
            if (old != null) {
                this._handleDuplicateField(fieldName, node, old, value);
            }
            t = jp.nextToken();
        }
        return node;
    }

    protected final ArrayNode deserializeArray(JsonParser jp, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException, JsonProcessingException {
        ArrayNode node = nodeFactory.arrayNode();
        block6: while (true) {
            switch (jp.nextToken()) {
                case START_OBJECT: {
                    node.add(this.deserializeObject(jp, ctxt, nodeFactory));
                    continue block6;
                }
                case START_ARRAY: {
                    node.add(this.deserializeArray(jp, ctxt, nodeFactory));
                    continue block6;
                }
                case END_ARRAY: {
                    return node;
                }
                case VALUE_STRING: {
                    node.add(nodeFactory.textNode(jp.getText()));
                    continue block6;
                }
            }
            node.add(this.deserializeAny(jp, ctxt, nodeFactory));
        }
    }

    protected final JsonNode deserializeAny(JsonParser jp, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException, JsonProcessingException {
        switch (jp.getCurrentToken()) {
            case START_OBJECT: {
                return this.deserializeObject(jp, ctxt, nodeFactory);
            }
            case START_ARRAY: {
                return this.deserializeArray(jp, ctxt, nodeFactory);
            }
            case FIELD_NAME: {
                return this.deserializeObject(jp, ctxt, nodeFactory);
            }
            case VALUE_EMBEDDED_OBJECT: {
                Object ob = jp.getEmbeddedObject();
                if (ob == null) {
                    return nodeFactory.nullNode();
                }
                Class<?> type = ob.getClass();
                if (type == byte[].class) {
                    return nodeFactory.binaryNode((byte[])ob);
                }
                return nodeFactory.POJONode(ob);
            }
            case VALUE_STRING: {
                return nodeFactory.textNode(jp.getText());
            }
            case VALUE_NUMBER_INT: {
                JsonParser.NumberType nt = jp.getNumberType();
                if (nt == JsonParser.NumberType.BIG_INTEGER || ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS)) {
                    return nodeFactory.numberNode(jp.getBigIntegerValue());
                }
                if (nt == JsonParser.NumberType.INT) {
                    return nodeFactory.numberNode(jp.getIntValue());
                }
                return nodeFactory.numberNode(jp.getLongValue());
            }
            case VALUE_NUMBER_FLOAT: {
                JsonParser.NumberType nt = jp.getNumberType();
                if (nt == JsonParser.NumberType.BIG_DECIMAL || ctxt.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return nodeFactory.numberNode(jp.getDecimalValue());
                }
                return nodeFactory.numberNode(jp.getDoubleValue());
            }
            case VALUE_TRUE: {
                return nodeFactory.booleanNode(true);
            }
            case VALUE_FALSE: {
                return nodeFactory.booleanNode(false);
            }
            case VALUE_NULL: {
                return nodeFactory.nullNode();
            }
        }
        throw ctxt.mappingException(this.getValueClass());
    }
}

