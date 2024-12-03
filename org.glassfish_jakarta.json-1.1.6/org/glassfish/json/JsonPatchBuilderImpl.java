/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import org.glassfish.json.JsonPatchImpl;

public final class JsonPatchBuilderImpl
implements JsonPatchBuilder {
    private final JsonArrayBuilder builder;

    public JsonPatchBuilderImpl(JsonArray patch) {
        this.builder = Json.createArrayBuilder(patch);
    }

    public JsonPatchBuilderImpl() {
        this.builder = Json.createArrayBuilder();
    }

    public <T extends JsonStructure> T apply(T target) {
        return this.build().apply(target);
    }

    @Override
    public JsonPatchBuilder add(String path, JsonValue value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.ADD.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder add(String path, String value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.ADD.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder add(String path, int value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.ADD.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder add(String path, boolean value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.ADD.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder remove(String path) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.REMOVE.operationName()).add("path", path));
        return this;
    }

    @Override
    public JsonPatchBuilder replace(String path, JsonValue value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.REPLACE.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder replace(String path, String value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.REPLACE.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder replace(String path, int value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.REPLACE.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder replace(String path, boolean value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.REPLACE.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder move(String path, String from) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.MOVE.operationName()).add("path", path).add("from", from));
        return this;
    }

    @Override
    public JsonPatchBuilder copy(String path, String from) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.COPY.operationName()).add("path", path).add("from", from));
        return this;
    }

    @Override
    public JsonPatchBuilder test(String path, JsonValue value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.TEST.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder test(String path, String value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.TEST.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder test(String path, int value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.TEST.operationName()).add("path", path).add("value", value));
        return this;
    }

    @Override
    public JsonPatchBuilder test(String path, boolean value) {
        this.builder.add(Json.createObjectBuilder().add("op", JsonPatch.Operation.TEST.operationName()).add("path", path).add("value", value));
        return this;
    }

    public JsonArray buildAsJsonArray() {
        return this.builder.build();
    }

    @Override
    public JsonPatch build() {
        return new JsonPatchImpl(this.buildAsJsonArray());
    }
}

