/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import org.glassfish.json.JsonMessages;

abstract class NodeReference {
    NodeReference() {
    }

    public abstract boolean contains();

    public abstract JsonValue get();

    public abstract JsonStructure add(JsonValue var1);

    public abstract JsonStructure remove();

    public abstract JsonStructure replace(JsonValue var1);

    public static NodeReference of(JsonStructure structure) {
        return new RootReference(structure);
    }

    public static NodeReference of(JsonObject object, String name) {
        return new ObjectReference(object, name);
    }

    public static NodeReference of(JsonArray array, int index) {
        return new ArrayReference(array, index);
    }

    static class ArrayReference
    extends NodeReference {
        private final JsonArray array;
        private final int index;

        ArrayReference(JsonArray array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public boolean contains() {
            return this.array != null && this.index > -1 && this.index < this.array.size();
        }

        @Override
        public JsonValue get() {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
            }
            return (JsonValue)this.array.get(this.index);
        }

        @Override
        public JsonArray add(JsonValue value) {
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            if (this.index == -1 || this.index == this.array.size()) {
                builder.add(value);
            } else if (this.index < this.array.size()) {
                builder.add(this.index, value);
            } else {
                throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
            }
            return builder.build();
        }

        @Override
        public JsonArray remove() {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
            }
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            return builder.remove(this.index).build();
        }

        @Override
        public JsonArray replace(JsonValue value) {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_ARRAY_INDEX_ERR(this.index, this.array.size()));
            }
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            return builder.set(this.index, value).build();
        }
    }

    static class ObjectReference
    extends NodeReference {
        private final JsonObject object;
        private final String key;

        ObjectReference(JsonObject object, String key) {
            this.object = object;
            this.key = key;
        }

        @Override
        public boolean contains() {
            return this.object != null && this.object.containsKey(this.key);
        }

        @Override
        public JsonValue get() {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
            }
            return (JsonValue)this.object.get(this.key);
        }

        @Override
        public JsonObject add(JsonValue value) {
            return Json.createObjectBuilder(this.object).add(this.key, value).build();
        }

        @Override
        public JsonObject remove() {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
            }
            return Json.createObjectBuilder(this.object).remove(this.key).build();
        }

        @Override
        public JsonObject replace(JsonValue value) {
            if (!this.contains()) {
                throw new JsonException(JsonMessages.NODEREF_OBJECT_MISSING(this.key));
            }
            return this.add(value);
        }
    }

    static class RootReference
    extends NodeReference {
        private JsonStructure root;

        RootReference(JsonStructure root) {
            this.root = root;
        }

        @Override
        public boolean contains() {
            return this.root != null;
        }

        @Override
        public JsonValue get() {
            return this.root;
        }

        @Override
        public JsonStructure add(JsonValue value) {
            switch (value.getValueType()) {
                case OBJECT: 
                case ARRAY: {
                    this.root = (JsonStructure)value;
                    break;
                }
                default: {
                    throw new JsonException(JsonMessages.NODEREF_VALUE_ADD_ERR());
                }
            }
            return this.root;
        }

        @Override
        public JsonStructure remove() {
            throw new JsonException(JsonMessages.NODEREF_VALUE_CANNOT_REMOVE());
        }

        @Override
        public JsonStructure replace(JsonValue value) {
            return this.add(value);
        }
    }
}

