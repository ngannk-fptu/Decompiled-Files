/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.InternalNodeMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NodeSerialization;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.IOException;
import java.io.Serializable;

public abstract class BaseJsonNode
extends JsonNode
implements Serializable {
    private static final long serialVersionUID = 1L;

    Object writeReplace() {
        return NodeSerialization.from(this);
    }

    protected BaseJsonNode() {
    }

    @Override
    public final JsonNode findPath(String fieldName) {
        JsonNode value = this.findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }

    public abstract int hashCode();

    @Override
    public JsonNode required(String fieldName) {
        return (JsonNode)this._reportRequiredViolation("Node of type `%s` has no fields", this.getClass().getSimpleName());
    }

    @Override
    public JsonNode required(int index) {
        return (JsonNode)this._reportRequiredViolation("Node of type `%s` has no indexed values", this.getClass().getSimpleName());
    }

    @Override
    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }

    @Override
    public JsonParser traverse(ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }

    @Override
    public abstract JsonToken asToken();

    @Override
    public JsonParser.NumberType numberType() {
        return null;
    }

    @Override
    public ObjectNode withObject(JsonPointer ptr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        ObjectNode n;
        if (ptr.matches()) {
            if (this instanceof ObjectNode) {
                return (ObjectNode)this;
            }
            this._reportWrongNodeType("Can only call `withObject()` with empty JSON Pointer on `ObjectNode`, not `%s`", this.getClass().getName());
        }
        if ((n = this._withObject(ptr, ptr, overwriteMode, preferIndex)) == null) {
            this._reportWrongNodeType("Cannot replace context node (of type `%s`) using `withObject()` with  JSON Pointer '%s'", this.getClass().getName(), ptr);
        }
        return n;
    }

    protected ObjectNode _withObject(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        return null;
    }

    protected void _withXxxVerifyReplace(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex, JsonNode toReplace) {
        if (!this._withXxxMayReplace(toReplace, overwriteMode)) {
            this._reportWrongNodeType("Cannot replace `JsonNode` of type `%s` for property \"%s\" in JSON Pointer \"%s\" (mode `OverwriteMode.%s`)", new Object[]{toReplace.getClass().getName(), currentPtr.getMatchingProperty(), origPtr, overwriteMode});
        }
    }

    protected boolean _withXxxMayReplace(JsonNode node, JsonNode.OverwriteMode overwriteMode) {
        switch (overwriteMode) {
            case NONE: {
                return false;
            }
            case NULLS: {
                return node.isNull();
            }
            case SCALARS: {
                return !node.isContainerNode();
            }
        }
        return true;
    }

    @Override
    public ArrayNode withArray(JsonPointer ptr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        ArrayNode n;
        if (ptr.matches()) {
            if (this instanceof ArrayNode) {
                return (ArrayNode)this;
            }
            this._reportWrongNodeType("Can only call `withArray()` with empty JSON Pointer on `ArrayNode`, not `%s`", this.getClass().getName());
        }
        if ((n = this._withArray(ptr, ptr, overwriteMode, preferIndex)) == null) {
            this._reportWrongNodeType("Cannot replace context node (of type `%s`) using `withArray()` with  JSON Pointer '%s'", this.getClass().getName(), ptr);
        }
        return n;
    }

    protected ArrayNode _withArray(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        return null;
    }

    @Override
    public abstract void serialize(JsonGenerator var1, SerializerProvider var2) throws IOException;

    @Override
    public abstract void serializeWithType(JsonGenerator var1, SerializerProvider var2, TypeSerializer var3) throws IOException;

    @Override
    public String toString() {
        return InternalNodeMapper.nodeToString(this);
    }

    @Override
    public String toPrettyString() {
        return InternalNodeMapper.nodeToPrettyString(this);
    }

    protected <T> T _reportWrongNodeType(String msgTemplate, Object ... args) {
        throw new UnsupportedOperationException(String.format(msgTemplate, args));
    }

    protected <T> T _reportWrongNodeOperation(String msgTemplate, Object ... args) {
        throw new UnsupportedOperationException(String.format(msgTemplate, args));
    }

    protected JsonPointer _jsonPointerIfValid(String exprOrProperty) {
        if (exprOrProperty.isEmpty() || exprOrProperty.charAt(0) == '/') {
            return JsonPointer.compile(exprOrProperty);
        }
        return null;
    }
}

