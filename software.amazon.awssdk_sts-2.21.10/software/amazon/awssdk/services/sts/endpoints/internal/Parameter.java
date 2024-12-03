/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.sts.endpoints.internal.BooleanEqualsFn;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;
import software.amazon.awssdk.services.sts.endpoints.internal.ParameterReference;
import software.amazon.awssdk.services.sts.endpoints.internal.ParameterType;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleError;
import software.amazon.awssdk.services.sts.endpoints.internal.ToParameterReference;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

@SdkInternalApi
public final class Parameter
implements ToParameterReference {
    public static final String TYPE = "type";
    public static final String DEPRECATED = "deprecated";
    public static final String DOCUMENTATION = "documentation";
    public static final String DEFAULT = "default";
    private static final String BUILT_IN = "builtIn";
    private static final String REQUIRED = "required";
    private final ParameterType type;
    private final Identifier name;
    private final Value value;
    private final String builtIn;
    private final Value defaultValue;
    private final Deprecated deprecated;
    private final String documentation;
    private final boolean required;

    public Parameter(Builder builder) {
        if (builder.defaultValue != null && builder.builtIn == null) {
            throw new RuntimeException("Cannot set a default value for non-builtin parameters");
        }
        if (builder.defaultValue != null && !builder.required) {
            throw new RuntimeException("When a default value is set, the field must also be marked as required");
        }
        this.type = builder.type;
        this.name = builder.name;
        this.builtIn = builder.builtIn;
        this.value = builder.value;
        this.required = builder.required;
        this.deprecated = builder.deprecated;
        this.documentation = builder.documentation;
        this.defaultValue = builder.defaultValue;
    }

    public Optional<String> getBuiltIn() {
        return Optional.ofNullable(this.builtIn);
    }

    public Optional<Value> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue);
    }

    public boolean isRequired() {
        return this.required;
    }

    public Optional<Deprecated> getDeprecated() {
        return Optional.ofNullable(this.deprecated);
    }

    public static Parameter fromNode(String name, JsonNode node) throws RuleError {
        JsonNode required;
        JsonNode defaultNode;
        JsonNode documentation;
        Map objNode = node.asObject();
        Builder b = Parameter.builder();
        b.name(name);
        b.type(ParameterType.fromNode((JsonNode)objNode.get(TYPE)));
        JsonNode builtIn = (JsonNode)objNode.get(BUILT_IN);
        if (builtIn != null) {
            b.builtIn(builtIn.asString());
        }
        if ((documentation = (JsonNode)objNode.get(DOCUMENTATION)) != null) {
            b.documentation(documentation.asString());
        }
        if ((defaultNode = (JsonNode)objNode.get(DEFAULT)) != null) {
            b.defaultValue(Value.fromNode(defaultNode));
        }
        if ((required = (JsonNode)objNode.get(REQUIRED)) != null) {
            b.required(required.asBoolean());
        } else {
            b.required(false);
        }
        JsonNode deprecated = (JsonNode)objNode.get(DEPRECATED);
        if (deprecated != null) {
            b.deprecated(Deprecated.fromNode(deprecated));
        }
        return b.build();
    }

    public ParameterType getType() {
        return this.type;
    }

    public Identifier getName() {
        return this.name;
    }

    public boolean isBuiltIn() {
        return this.builtIn != null;
    }

    public Optional<Value> getValue() {
        return Optional.ofNullable(this.value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(": ").append((Object)this.type);
        if (this.builtIn != null) {
            sb.append("; builtIn(").append(this.builtIn).append(")");
        }
        if (this.required) {
            sb.append("; required");
        }
        this.getDeprecated().ifPresent(dep -> sb.append("; ").append(this.deprecated).append("!"));
        return sb.toString();
    }

    @Override
    public ParameterReference toParameterReference() {
        return ParameterReference.builder().name(this.getName().asString()).build();
    }

    public String template() {
        return "{" + this.name + "}";
    }

    public Expr expr() {
        return Expr.ref(this.name);
    }

    public BooleanEqualsFn eq(boolean b) {
        return BooleanEqualsFn.fromParam(this, Expr.of(b));
    }

    public BooleanEqualsFn eq(Expr e) {
        return BooleanEqualsFn.fromParam(this, e);
    }

    public Optional<String> getDocumentation() {
        return Optional.ofNullable(this.documentation);
    }

    public Optional<Value> getDefault() {
        return Optional.ofNullable(this.defaultValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Parameter parameter = (Parameter)o;
        if (this.required != parameter.required) {
            return false;
        }
        if (this.type != parameter.type) {
            return false;
        }
        if (this.name != null ? !this.name.equals(parameter.name) : parameter.name != null) {
            return false;
        }
        if (this.value != null ? !this.value.equals(parameter.value) : parameter.value != null) {
            return false;
        }
        if (this.builtIn != null ? !this.builtIn.equals(parameter.builtIn) : parameter.builtIn != null) {
            return false;
        }
        if (this.defaultValue != null ? !this.defaultValue.equals(parameter.defaultValue) : parameter.defaultValue != null) {
            return false;
        }
        if (this.deprecated != null ? !this.deprecated.equals(parameter.deprecated) : parameter.deprecated != null) {
            return false;
        }
        return this.documentation != null ? this.documentation.equals(parameter.documentation) : parameter.documentation == null;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.builtIn != null ? this.builtIn.hashCode() : 0);
        result = 31 * result + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        result = 31 * result + (this.required ? 1 : 0);
        result = 31 * result + (this.deprecated != null ? this.deprecated.hashCode() : 0);
        result = 31 * result + (this.documentation != null ? this.documentation.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private ParameterType type;
        private Identifier name;
        private String builtIn;
        private Deprecated deprecated;
        private Value value;
        private boolean required;
        private String documentation;
        private Value defaultValue;

        public Builder type(ParameterType type) {
            this.type = type;
            return this;
        }

        public Builder deprecated(Deprecated deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        public Builder name(String name) {
            this.name = Identifier.of(name);
            return this;
        }

        public Builder name(Identifier name) {
            this.name = name;
            return this;
        }

        public Builder builtIn(String builtIn) {
            this.builtIn = builtIn;
            return this;
        }

        public Builder value(Value value) {
            this.value = value;
            return this;
        }

        public Builder defaultValue(Value defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Parameter build() {
            return new Parameter(this);
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder documentation(String s) {
            this.documentation = s;
            return this;
        }
    }

    public static final class Deprecated {
        private static final String MESSAGE = "message";
        private static final String SINCE = "since";
        private final String message;
        private final String since;

        public Deprecated(String message, String since) {
            this.message = message;
            this.since = since;
        }

        public static Deprecated fromNode(JsonNode node) {
            JsonNode sinceNode;
            Map objNode = node.asObject();
            String message = null;
            String since = null;
            JsonNode messageNode = (JsonNode)objNode.get(MESSAGE);
            if (messageNode != null) {
                message = messageNode.asString();
            }
            if ((sinceNode = (JsonNode)objNode.get(SINCE)) != null) {
                since = sinceNode.asString();
            }
            return new Deprecated(message, since);
        }

        public String message() {
            return this.message;
        }

        public String since() {
            return this.since;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Deprecated that = (Deprecated)o;
            if (this.message != null ? !this.message.equals(that.message) : that.message != null) {
                return false;
            }
            return this.since != null ? this.since.equals(that.since) : that.since == null;
        }

        public int hashCode() {
            int result = this.message != null ? this.message.hashCode() : 0;
            result = 31 * result + (this.since != null ? this.since.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "Deprecated[message=" + this.message + ", since=" + this.since + ']';
        }
    }
}

