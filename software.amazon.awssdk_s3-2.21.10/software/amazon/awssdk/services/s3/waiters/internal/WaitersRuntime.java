/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.exception.SdkServiceException
 *  software.amazon.awssdk.core.waiters.WaiterAcceptor
 *  software.amazon.awssdk.core.waiters.WaiterState
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.services.s3.waiters.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterState;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class WaitersRuntime {
    public static final List<WaiterAcceptor<Object>> DEFAULT_ACCEPTORS = Collections.unmodifiableList(WaitersRuntime.defaultAcceptors());

    private WaitersRuntime() {
    }

    private static List<WaiterAcceptor<Object>> defaultAcceptors() {
        return Collections.singletonList(WaitersRuntime.retryOnUnmatchedResponseWaiter());
    }

    private static WaiterAcceptor<Object> retryOnUnmatchedResponseWaiter() {
        return WaiterAcceptor.retryOnResponseAcceptor(r -> true);
    }

    public static final class ResponseStatusAcceptor
    implements WaiterAcceptor<SdkResponse> {
        private final int statusCode;
        private final WaiterState waiterState;

        public ResponseStatusAcceptor(int statusCode, WaiterState waiterState) {
            this.statusCode = statusCode;
            this.waiterState = waiterState;
        }

        public WaiterState waiterState() {
            return this.waiterState;
        }

        public boolean matches(SdkResponse response) {
            return response.sdkHttpResponse() != null && response.sdkHttpResponse().statusCode() == this.statusCode;
        }

        public boolean matches(Throwable throwable) {
            if (throwable instanceof SdkServiceException) {
                return ((SdkServiceException)throwable).statusCode() == this.statusCode;
            }
            return false;
        }
    }

    public static final class Value {
        private static final Value NULL_VALUE = new Value(null);
        private final Type type;
        private final boolean isProjection;
        private SdkPojo pojoValue;
        private Integer integerValue;
        private String stringValue;
        private List<Object> listValue;
        private Boolean booleanValue;

        private Value(Collection<?> value, boolean projection) {
            this.type = Type.LIST;
            this.listValue = new ArrayList(value);
            this.isProjection = projection;
        }

        public Value(Object value) {
            this.isProjection = false;
            if (value == null) {
                this.type = Type.NULL;
            } else if (value instanceof SdkPojo) {
                this.type = Type.POJO;
                this.pojoValue = (SdkPojo)value;
            } else if (value instanceof String) {
                this.type = Type.STRING;
                this.stringValue = (String)value;
            } else if (value instanceof Integer) {
                this.type = Type.INTEGER;
                this.integerValue = (Integer)value;
            } else if (value instanceof Collection) {
                this.type = Type.LIST;
                this.listValue = new ArrayList<Object>((Collection)value);
            } else if (value instanceof Boolean) {
                this.type = Type.BOOLEAN;
                this.booleanValue = (Boolean)value;
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }
        }

        private static Value newProjection(Collection<?> values) {
            return new Value(values, true);
        }

        public Object value() {
            switch (this.type) {
                case NULL: {
                    return null;
                }
                case POJO: {
                    return this.pojoValue;
                }
                case INTEGER: {
                    return this.integerValue;
                }
                case STRING: {
                    return this.stringValue;
                }
                case BOOLEAN: {
                    return this.booleanValue;
                }
                case LIST: {
                    return this.listValue;
                }
            }
            throw new IllegalStateException();
        }

        public List<Object> values() {
            if (this.type == Type.NULL) {
                return Collections.emptyList();
            }
            if (this.type == Type.LIST) {
                return this.listValue;
            }
            return Collections.singletonList(this.value());
        }

        public Value constant(Value value) {
            return value;
        }

        public Value constant(Object constant) {
            return new Value(constant);
        }

        public Value wildcard() {
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type != Type.POJO) {
                throw new IllegalArgumentException("Cannot flatten a " + (Object)((Object)this.type));
            }
            return Value.newProjection(this.pojoValue.sdkFields().stream().map(f -> f.getValueOrDefault((Object)this.pojoValue)).filter(Objects::nonNull).collect(Collectors.toList()));
        }

        public Value flatten() {
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type != Type.LIST) {
                throw new IllegalArgumentException("Cannot flatten a " + (Object)((Object)this.type));
            }
            ArrayList<Object> result = new ArrayList<Object>();
            for (Object listEntry : this.listValue) {
                Value listValue = new Value(listEntry);
                if (listValue.type != Type.LIST) {
                    result.add(listEntry);
                    continue;
                }
                result.addAll(listValue.listValue);
            }
            return Value.newProjection(result);
        }

        public Value field(String fieldName) {
            if (this.isProjection) {
                return this.project(v -> v.field(fieldName));
            }
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type == Type.POJO) {
                return this.pojoValue.sdkFields().stream().filter((? super T f) -> f.memberName().equals(fieldName)).map(f -> f.getValueOrDefault((Object)this.pojoValue)).map(Value::new).findAny().orElseThrow(() -> new IllegalArgumentException("No such field: " + fieldName));
            }
            throw new IllegalArgumentException("Cannot get a field from a " + (Object)((Object)this.type));
        }

        public Value filter(Function<Value, Value> predicate) {
            if (this.isProjection) {
                return this.project(f -> f.filter(predicate));
            }
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type != Type.LIST) {
                throw new IllegalArgumentException("Unsupported type for filter function: " + (Object)((Object)this.type));
            }
            ArrayList results = new ArrayList();
            this.listValue.forEach(entry -> {
                Value entryValue = new Value(entry);
                Value predicateResult = (Value)predicate.apply(entryValue);
                if (predicateResult.isTrue()) {
                    results.add(entry);
                }
            });
            return new Value(results);
        }

        public Value length() {
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type == Type.STRING) {
                return new Value(this.stringValue.length());
            }
            if (this.type == Type.POJO) {
                return new Value(this.pojoValue.sdkFields().size());
            }
            if (this.type == Type.LIST) {
                return new Value(Math.toIntExact(this.listValue.size()));
            }
            throw new IllegalArgumentException("Unsupported type for length function: " + (Object)((Object)this.type));
        }

        public Value contains(Value rhs) {
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            if (this.type == Type.STRING) {
                if (rhs.type != Type.STRING) {
                    return new Value(false);
                }
                return new Value(this.stringValue.contains(rhs.stringValue));
            }
            if (this.type == Type.LIST) {
                return new Value(this.listValue.stream().anyMatch(v -> Objects.equals(v, rhs.value())));
            }
            throw new IllegalArgumentException("Unsupported type for contains function: " + (Object)((Object)this.type));
        }

        public Value compare(String comparison, Value rhs) {
            if (this.type != rhs.type) {
                return new Value(false);
            }
            if (this.type == Type.INTEGER) {
                switch (comparison) {
                    case "<": {
                        return new Value(this.integerValue < rhs.integerValue);
                    }
                    case "<=": {
                        return new Value(this.integerValue <= rhs.integerValue);
                    }
                    case ">": {
                        return new Value(this.integerValue > rhs.integerValue);
                    }
                    case ">=": {
                        return new Value(this.integerValue >= rhs.integerValue);
                    }
                    case "==": {
                        return new Value(Objects.equals(this.integerValue, rhs.integerValue));
                    }
                    case "!=": {
                        return new Value(!Objects.equals(this.integerValue, rhs.integerValue));
                    }
                }
                throw new IllegalArgumentException("Unsupported comparison: " + comparison);
            }
            if (this.type == Type.NULL || this.type == Type.STRING || this.type == Type.BOOLEAN) {
                switch (comparison) {
                    case "<": 
                    case "<=": 
                    case ">": 
                    case ">=": {
                        return NULL_VALUE;
                    }
                    case "==": {
                        return new Value(Objects.equals(this.value(), rhs.value()));
                    }
                    case "!=": {
                        return new Value(!Objects.equals(this.value(), rhs.value()));
                    }
                }
                throw new IllegalArgumentException("Unsupported comparison: " + comparison);
            }
            throw new IllegalArgumentException("Unsupported type in comparison: " + (Object)((Object)this.type));
        }

        @SafeVarargs
        public final Value multiSelectList(Function<Value, Value> ... functions) {
            if (this.isProjection) {
                return this.project(v -> v.multiSelectList(functions));
            }
            if (this.type == Type.NULL) {
                return NULL_VALUE;
            }
            ArrayList<Object> result = new ArrayList<Object>();
            for (Function<Value, Value> function : functions) {
                result.add(function.apply(this).value());
            }
            return new Value(result);
        }

        public Value or(Value rhs) {
            if (this.isTrue()) {
                return this;
            }
            return rhs.isTrue() ? rhs : NULL_VALUE;
        }

        public Value and(Value rhs) {
            return this.isTrue() ? rhs : this;
        }

        public Value not() {
            return new Value(!this.isTrue());
        }

        private boolean isTrue() {
            switch (this.type) {
                case POJO: {
                    return !this.pojoValue.sdkFields().isEmpty();
                }
                case LIST: {
                    return !this.listValue.isEmpty();
                }
                case STRING: {
                    return !this.stringValue.isEmpty();
                }
                case BOOLEAN: {
                    return this.booleanValue;
                }
            }
            return false;
        }

        private Value project(Function<Value, Value> functionToApply) {
            return new Value(this.listValue.stream().map(Value::new).map(functionToApply).map(Value::value).collect(Collectors.toList()), true);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Value value = (Value)o;
            return this.type == value.type && Objects.equals(this.value(), value.value());
        }

        public int hashCode() {
            Object value = this.value();
            int result = this.type.hashCode();
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        public String toString() {
            return ToString.builder((String)"Value").add("type", (Object)this.type).add("value", this.value()).build();
        }

        private static enum Type {
            POJO,
            LIST,
            BOOLEAN,
            STRING,
            INTEGER,
            NULL;

        }
    }
}

