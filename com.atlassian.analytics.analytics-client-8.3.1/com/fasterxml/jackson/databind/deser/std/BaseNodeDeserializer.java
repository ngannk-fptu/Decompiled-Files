/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.util.Arrays;

abstract class BaseNodeDeserializer<T extends JsonNode>
extends StdDeserializer<T> {
    protected final Boolean _supportsUpdates;

    public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
        super(vc);
        this._supportsUpdates = supportsUpdates;
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._supportsUpdates;
    }

    protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for `ObjectNode`: not allowed when `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY` enabled", fieldName);
        }
        if (ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)) {
            if (oldValue.isArray()) {
                ((ArrayNode)oldValue).add(newValue);
                objectNode.replace(fieldName, oldValue);
            } else {
                ArrayNode arr = nodeFactory.arrayNode();
                arr.add(oldValue);
                arr.add(newValue);
                objectNode.replace(fieldName, arr);
            }
        }
    }

    protected final ObjectNode _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack) throws IOException {
        ObjectNode node = nodeFactory.objectNode();
        String key = p.currentName();
        while (key != null) {
            ContainerNode<ContainerNode> value;
            JsonToken t = p.nextToken();
            if (t == null) {
                t = JsonToken.NOT_AVAILABLE;
            }
            switch (t.id()) {
                case 1: {
                    value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
                    break;
                }
                case 3: {
                    value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
                    break;
                }
                default: {
                    value = this._deserializeAnyScalar(p, ctxt);
                }
            }
            JsonNode old = node.replace(key, value);
            if (old != null) {
                this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
            }
            key = p.nextFieldName();
        }
        return node;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node, ContainerStack stack) throws IOException {
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        } else {
            if (!p.hasToken(JsonToken.FIELD_NAME)) {
                return (JsonNode)this.deserialize(p, ctxt);
            }
            key = p.currentName();
        }
        nodeFactory = ctxt.getNodeFactory();
        while (key != null) {
            block18: {
                block17: {
                    t = p.nextToken();
                    old = node.get(key);
                    if (old == null) ** GOTO lbl-1000
                    if (!(old instanceof ObjectNode)) break block17;
                    if (t != JsonToken.START_OBJECT) ** GOTO lbl-1000
                    newValue = this.updateObject(p, ctxt, (ObjectNode)old, stack);
                    if (newValue != old) {
                        node.set(key, newValue);
                    }
                    break block18;
                }
                if (old instanceof ArrayNode && t == JsonToken.START_ARRAY) {
                    this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ArrayNode)old);
                } else lbl-1000:
                // 3 sources

                {
                    if (t == null) {
                        t = JsonToken.NOT_AVAILABLE;
                    }
                    switch (t.id()) {
                        case 1: {
                            value /* !! */  = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
                            break;
                        }
                        case 3: {
                            value /* !! */  = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
                            break;
                        }
                        case 6: {
                            value /* !! */  = nodeFactory.textNode(p.getText());
                            break;
                        }
                        case 7: {
                            value /* !! */  = this._fromInt(p, ctxt, nodeFactory);
                            break;
                        }
                        case 9: {
                            value /* !! */  = nodeFactory.booleanNode(true);
                            break;
                        }
                        case 10: {
                            value /* !! */  = nodeFactory.booleanNode(false);
                            break;
                        }
                        case 11: {
                            value /* !! */  = nodeFactory.nullNode();
                            break;
                        }
                        default: {
                            value /* !! */  = this._deserializeRareScalar(p, ctxt);
                        }
                    }
                    node.set(key, value /* !! */ );
                }
            }
            key = p.nextFieldName();
        }
        return node;
    }

    protected final ContainerNode<?> _deserializeContainerNoRecursion(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack, ContainerNode<?> root) throws IOException {
        ContainerNode curr = root;
        int intCoercionFeats = ctxt.getDeserializationFeatures() & F_MASK_INT_COERCIONS;
        block21: do {
            block31: {
                block29: {
                    if (!(curr instanceof ObjectNode)) break block29;
                    ObjectNode currObject = (ObjectNode)curr;
                    String propName = p.nextFieldName();
                    while (propName != null) {
                        block30: {
                            JsonNode value;
                            JsonToken t = p.nextToken();
                            if (t == null) {
                                t = JsonToken.NOT_AVAILABLE;
                            }
                            switch (t.id()) {
                                case 1: {
                                    ContainerNode newOb = nodeFactory.objectNode();
                                    JsonNode old = currObject.replace(propName, newOb);
                                    if (old != null) {
                                        this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, newOb);
                                    }
                                    stack.push(curr);
                                    currObject = newOb;
                                    curr = currObject;
                                    break block30;
                                }
                                case 3: {
                                    ContainerNode newOb = nodeFactory.arrayNode();
                                    JsonNode old = currObject.replace(propName, newOb);
                                    if (old != null) {
                                        this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, newOb);
                                    }
                                    stack.push(curr);
                                    curr = newOb;
                                    continue block21;
                                }
                                case 6: {
                                    value = nodeFactory.textNode(p.getText());
                                    break;
                                }
                                case 7: {
                                    value = this._fromInt(p, intCoercionFeats, nodeFactory);
                                    break;
                                }
                                case 8: {
                                    value = this._fromFloat(p, ctxt, nodeFactory);
                                    break;
                                }
                                case 9: {
                                    value = nodeFactory.booleanNode(true);
                                    break;
                                }
                                case 10: {
                                    value = nodeFactory.booleanNode(false);
                                    break;
                                }
                                case 11: {
                                    value = nodeFactory.nullNode();
                                    break;
                                }
                                default: {
                                    value = this._deserializeRareScalar(p, ctxt);
                                }
                            }
                            JsonNode old = currObject.replace(propName, value);
                            if (old != null) {
                                this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, value);
                            }
                        }
                        propName = p.nextFieldName();
                    }
                    break block31;
                }
                ArrayNode currArray = (ArrayNode)curr;
                block23: while (true) {
                    JsonToken t;
                    if ((t = p.nextToken()) == null) {
                        t = JsonToken.NOT_AVAILABLE;
                    }
                    switch (t.id()) {
                        case 1: {
                            stack.push(curr);
                            curr = nodeFactory.objectNode();
                            currArray.add(curr);
                            continue block21;
                        }
                        case 3: {
                            stack.push(curr);
                            curr = nodeFactory.arrayNode();
                            currArray.add(curr);
                            continue block21;
                        }
                        case 4: {
                            break block23;
                        }
                        case 6: {
                            currArray.add(nodeFactory.textNode(p.getText()));
                            continue block23;
                        }
                        case 7: {
                            currArray.add(this._fromInt(p, intCoercionFeats, nodeFactory));
                            continue block23;
                        }
                        case 8: {
                            currArray.add(this._fromFloat(p, ctxt, nodeFactory));
                            continue block23;
                        }
                        case 9: {
                            currArray.add(nodeFactory.booleanNode(true));
                            continue block23;
                        }
                        case 10: {
                            currArray.add(nodeFactory.booleanNode(false));
                            continue block23;
                        }
                        case 11: {
                            currArray.add(nodeFactory.nullNode());
                            continue block23;
                        }
                        default: {
                            currArray.add(this._deserializeRareScalar(p, ctxt));
                            continue block23;
                        }
                    }
                    break;
                }
            }
            curr = stack.popOrNull();
        } while (curr != null);
        return root;
    }

    protected final JsonNode _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNodeFactory nodeF = ctxt.getNodeFactory();
        switch (p.currentTokenId()) {
            case 2: {
                return nodeF.objectNode();
            }
            case 6: {
                return nodeF.textNode(p.getText());
            }
            case 7: {
                return this._fromInt(p, ctxt, nodeF);
            }
            case 8: {
                return this._fromFloat(p, ctxt, nodeF);
            }
            case 9: {
                return nodeF.booleanNode(true);
            }
            case 10: {
                return nodeF.booleanNode(false);
            }
            case 11: {
                return nodeF.nullNode();
            }
            case 12: {
                return this._fromEmbedded(p, ctxt);
            }
        }
        return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
    }

    protected final JsonNode _deserializeRareScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 2: {
                return ctxt.getNodeFactory().objectNode();
            }
            case 8: {
                return this._fromFloat(p, ctxt, ctxt.getNodeFactory());
            }
            case 12: {
                return this._fromEmbedded(p, ctxt);
            }
        }
        return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
    }

    protected final JsonNode _fromInt(JsonParser p, int coercionFeatures, JsonNodeFactory nodeFactory) throws IOException {
        if (coercionFeatures != 0) {
            if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(coercionFeatures)) {
                return nodeFactory.numberNode(p.getBigIntegerValue());
            }
            return nodeFactory.numberNode(p.getLongValue());
        }
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        int feats = ctxt.getDeserializationFeatures();
        JsonParser.NumberType nt = (feats & F_MASK_INT_COERCIONS) != 0 ? (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats) ? JsonParser.NumberType.BIG_INTEGER : (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats) ? JsonParser.NumberType.LONG : p.getNumberType())) : p.getNumberType();
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.BIG_DECIMAL) {
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            if (p.isNaN()) {
                return nodeFactory.numberNode(p.getDoubleValue());
            }
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        if (nt == JsonParser.NumberType.FLOAT) {
            return nodeFactory.numberNode(p.getFloatValue());
        }
        return nodeFactory.numberNode(p.getDoubleValue());
    }

    protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNodeFactory nodeF = ctxt.getNodeFactory();
        Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return nodeF.nullNode();
        }
        Class<?> type = ob.getClass();
        if (type == byte[].class) {
            return nodeF.binaryNode((byte[])ob);
        }
        if (ob instanceof RawValue) {
            return nodeF.rawValueNode((RawValue)ob);
        }
        if (ob instanceof JsonNode) {
            return (JsonNode)ob;
        }
        return nodeF.pojoNode(ob);
    }

    static final class ContainerStack {
        private ContainerNode[] _stack;
        private int _top;
        private int _end;

        public int size() {
            return this._top;
        }

        public void push(ContainerNode node) {
            if (this._top < this._end) {
                this._stack[this._top++] = node;
                return;
            }
            if (this._stack == null) {
                this._end = 10;
                this._stack = new ContainerNode[this._end];
            } else {
                this._end += Math.min(4000, Math.max(20, this._end >> 1));
                this._stack = Arrays.copyOf(this._stack, this._end);
            }
            this._stack[this._top++] = node;
        }

        public ContainerNode popOrNull() {
            if (this._top == 0) {
                return null;
            }
            return this._stack[--this._top];
        }
    }
}

