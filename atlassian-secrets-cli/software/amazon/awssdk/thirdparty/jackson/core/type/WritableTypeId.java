/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.type;

import software.amazon.awssdk.thirdparty.jackson.core.JsonToken;

public class WritableTypeId {
    public Object forValue;
    public Class<?> forValueType;
    public Object id;
    public String asProperty;
    public Inclusion include;
    public JsonToken valueShape;
    public boolean wrapperWritten;
    public Object extra;

    public WritableTypeId() {
    }

    public WritableTypeId(Object value, JsonToken valueShape) {
        this(value, valueShape, null);
    }

    public WritableTypeId(Object value, Class<?> valueType, JsonToken valueShape) {
        this(value, valueShape, null);
        this.forValueType = valueType;
    }

    public WritableTypeId(Object value, JsonToken valueShape, Object id) {
        this.forValue = value;
        this.id = id;
        this.valueShape = valueShape;
    }

    public static enum Inclusion {
        WRAPPER_ARRAY,
        WRAPPER_OBJECT,
        METADATA_PROPERTY,
        PAYLOAD_PROPERTY,
        PARENT_PROPERTY;


        public boolean requiresObjectContext() {
            return this == METADATA_PROPERTY || this == PAYLOAD_PROPERTY;
        }
    }
}

