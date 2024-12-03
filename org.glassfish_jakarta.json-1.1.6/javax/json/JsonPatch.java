/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonStructure;

public interface JsonPatch {
    public <T extends JsonStructure> T apply(T var1);

    public JsonArray toJsonArray();

    public static enum Operation {
        ADD("add"),
        REMOVE("remove"),
        REPLACE("replace"),
        MOVE("move"),
        COPY("copy"),
        TEST("test");

        private final String operationName;

        private Operation(String operationName) {
            this.operationName = operationName;
        }

        public String operationName() {
            return this.operationName;
        }

        public static Operation fromOperationName(String operationName) {
            for (Operation op : Operation.values()) {
                if (!op.operationName().equalsIgnoreCase(operationName)) continue;
                return op;
            }
            throw new JsonException("Illegal value for the operationName of the JSON patch operation: " + operationName);
        }
    }
}

