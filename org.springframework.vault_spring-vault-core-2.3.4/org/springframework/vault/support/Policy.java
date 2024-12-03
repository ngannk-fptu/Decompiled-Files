/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.databind.type.TypeFactory
 *  com.fasterxml.jackson.databind.util.Converter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@JsonSerialize(using=PolicySerializer.class)
@JsonDeserialize(using=PolicyDeserializer.class)
public class Policy {
    private static final Policy EMPTY = new Policy(Collections.emptySet());
    private final Set<Rule> rules;

    private Policy(Set<Rule> rules) {
        this.rules = rules;
    }

    public static Policy empty() {
        return EMPTY;
    }

    public static Policy of(Rule ... rules) {
        Assert.notNull((Object)rules, (String)"Rules must not be null");
        Assert.noNullElements((Object[])rules, (String)"Rules must not contain null elements");
        return new Policy(new LinkedHashSet<Rule>(Arrays.asList(rules)));
    }

    public static Policy of(Set<Rule> rules) {
        Assert.notNull(rules, (String)"Rules must not be null");
        return new Policy(new LinkedHashSet<Rule>(rules));
    }

    public Policy with(Rule rule) {
        Assert.notNull((Object)rule, (String)"Rule must not be null");
        LinkedHashSet<Rule> rules = new LinkedHashSet<Rule>(this.rules.size() + 1);
        rules.addAll(this.rules);
        rules.add(rule);
        return new Policy(rules);
    }

    public Set<Rule> getRules() {
        return this.rules;
    }

    @Nullable
    public Rule getRule(String path) {
        Assert.notNull((Object)path, (String)"Path must not be null");
        for (Rule rule : this.rules) {
            if (!rule.getPath().equals(path)) continue;
            return rule;
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Policy)) {
            return false;
        }
        Policy policy = (Policy)o;
        return this.rules.equals(policy.rules);
    }

    public int hashCode() {
        return Objects.hash(this.rules);
    }

    static class StringToDurationConverter
    implements Converter<String, Duration> {
        static Pattern SECONDS = Pattern.compile("(\\d+)s");
        static Pattern MINUTES = Pattern.compile("(\\d+)m");
        static Pattern HOURS = Pattern.compile("(\\d+)h");

        StringToDurationConverter() {
        }

        public Duration convert(String value) {
            try {
                return Duration.ofSeconds(Long.parseLong(value));
            }
            catch (NumberFormatException e) {
                Matcher matcher = SECONDS.matcher(value);
                if (matcher.matches()) {
                    return Duration.ofSeconds(Long.parseLong(matcher.group(1)));
                }
                matcher = MINUTES.matcher(value);
                if (matcher.matches()) {
                    return Duration.ofMinutes(Long.parseLong(matcher.group(1)));
                }
                matcher = HOURS.matcher(value);
                if (matcher.matches()) {
                    return Duration.ofHours(Long.parseLong(matcher.group(1)));
                }
                throw new IllegalArgumentException("Unsupported duration value: " + value);
            }
        }

        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Capability.class);
        }
    }

    static class DurationToStringConverter
    implements Converter<Duration, String> {
        DurationToStringConverter() {
        }

        public String convert(Duration value) {
            return "" + value.getSeconds();
        }

        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Duration.class);
        }

        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }
    }

    static class StringToCapabilityConverter
    implements Converter<String, Capability> {
        StringToCapabilityConverter() {
        }

        public Capability convert(String value) {
            Capability capability = BuiltinCapabilities.find(value);
            return capability != null ? capability : () -> value;
        }

        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Capability.class);
        }
    }

    static class CapabilityToStringConverter
    implements Converter<Capability, String> {
        CapabilityToStringConverter() {
        }

        public String convert(Capability value) {
            return value.name().toLowerCase();
        }

        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Capability.class);
        }

        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }
    }

    static class PolicyDeserializer
    extends JsonDeserializer<Policy> {
        PolicyDeserializer() {
        }

        public Policy deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Assert.isTrue((p.getCurrentToken() == JsonToken.START_OBJECT ? 1 : 0) != 0, (String)("Expected START_OBJECT, got: " + p.getCurrentToken()));
            String fieldName = p.nextFieldName();
            LinkedHashSet<Rule> rules = new LinkedHashSet<Rule>();
            if ("path".equals(fieldName)) {
                p.nextToken();
                Assert.isTrue((p.getCurrentToken() == JsonToken.START_OBJECT ? 1 : 0) != 0, (String)("Expected START_OBJECT, got: " + p.getCurrentToken()));
                p.nextToken();
                while (p.currentToken() == JsonToken.FIELD_NAME) {
                    String path = p.getCurrentName();
                    p.nextToken();
                    Assert.isTrue((p.getCurrentToken() == JsonToken.START_OBJECT ? 1 : 0) != 0, (String)("Expected START_OBJECT, got: " + p.getCurrentToken()));
                    Rule rule = (Rule)p.getCodec().readValue(p, Rule.class);
                    rules.add(rule.withPath(path));
                    JsonToken jsonToken = p.nextToken();
                    if (jsonToken != JsonToken.END_OBJECT) continue;
                    break;
                }
                Assert.isTrue((p.getCurrentToken() == JsonToken.END_OBJECT ? 1 : 0) != 0, (String)("Expected END_OBJECT, got: " + p.getCurrentToken()));
                p.nextToken();
            }
            Assert.isTrue((p.getCurrentToken() == JsonToken.END_OBJECT ? 1 : 0) != 0, (String)("Expected END_OBJECT, got: " + p.getCurrentToken()));
            return Policy.of(rules);
        }
    }

    static class PolicySerializer
    extends JsonSerializer<Policy> {
        PolicySerializer() {
        }

        public void serialize(Policy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName("path");
            gen.writeStartObject();
            for (Rule rule : value.getRules()) {
                gen.writeObjectField(rule.path, (Object)rule);
            }
            gen.writeEndObject();
            gen.writeEndObject();
        }
    }

    public static enum BuiltinCapabilities implements Capability
    {
        CREATE,
        READ,
        UPDATE,
        WRITE,
        DELETE,
        LIST,
        SUDO,
        DENY;


        @Nullable
        public static Capability find(String value) {
            for (BuiltinCapabilities cap : BuiltinCapabilities.values()) {
                if (!cap.name().equalsIgnoreCase(value)) continue;
                return cap;
            }
            return null;
        }

        public static List<Capability> crud() {
            return Arrays.asList(CREATE, READ, UPDATE, DELETE, LIST);
        }

        public static List<Capability> crudAndSudo() {
            return Arrays.asList(CREATE, READ, UPDATE, DELETE, LIST, SUDO);
        }
    }

    public static interface Capability {
        public String name();
    }

    @JsonInclude(value=JsonInclude.Include.NON_EMPTY)
    public static class Rule {
        @JsonIgnore
        private final String path;
        @JsonSerialize(contentConverter=CapabilityToStringConverter.class)
        @JsonDeserialize(contentConverter=StringToCapabilityConverter.class)
        private final List<Capability> capabilities;
        @JsonProperty(value="min_wrapping_ttl")
        @JsonSerialize(converter=DurationToStringConverter.class)
        @Nullable
        private final Duration minWrappingTtl;
        @JsonProperty(value="max_wrapping_ttl")
        @JsonSerialize(converter=DurationToStringConverter.class)
        @Nullable
        private final Duration maxWrappingTtl;
        @JsonProperty(value="allowed_parameters")
        private final Map<String, List<String>> allowedParameters;
        @JsonProperty(value="denied_parameters")
        private final Map<String, List<String>> deniedParameters;

        @JsonCreator
        private Rule(@JsonProperty(value="capabilities") List<Capability> capabilities, @JsonProperty(value="min_wrapping_ttl") @JsonDeserialize(converter=StringToDurationConverter.class) Duration minWrappingTtl, @JsonProperty(value="max_wrapping_ttl") @JsonDeserialize(converter=StringToDurationConverter.class) Duration maxWrappingTtl, @JsonProperty(value="allowed_parameters") Map<String, List<String>> allowedParameters, @JsonProperty(value="denied_parameters") Map<String, List<String>> deniedParameters) {
            this.path = "";
            this.capabilities = capabilities;
            this.minWrappingTtl = minWrappingTtl;
            this.maxWrappingTtl = maxWrappingTtl;
            this.allowedParameters = allowedParameters;
            this.deniedParameters = deniedParameters;
        }

        private Rule(String path, List<Capability> capabilities, @Nullable Duration minWrappingTtl, @Nullable Duration maxWrappingTtl, Map<String, List<String>> allowedParameters, Map<String, List<String>> deniedParameters) {
            this.path = path;
            this.capabilities = capabilities;
            this.minWrappingTtl = minWrappingTtl;
            this.maxWrappingTtl = maxWrappingTtl;
            this.allowedParameters = allowedParameters;
            this.deniedParameters = deniedParameters;
        }

        public static RuleBuilder builder() {
            return new RuleBuilder();
        }

        private Rule withPath(String path) {
            return new Rule(path, this.capabilities, this.minWrappingTtl, this.maxWrappingTtl, this.allowedParameters, this.deniedParameters);
        }

        public String getPath() {
            return this.path;
        }

        public List<Capability> getCapabilities() {
            return this.capabilities;
        }

        @Nullable
        public Duration getMinWrappingTtl() {
            return this.minWrappingTtl;
        }

        @Nullable
        public Duration getMaxWrappingTtl() {
            return this.maxWrappingTtl;
        }

        public Map<String, List<String>> getAllowedParameters() {
            return this.allowedParameters;
        }

        public Map<String, List<String>> getDeniedParameters() {
            return this.deniedParameters;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Rule)) {
                return false;
            }
            Rule rule = (Rule)o;
            return this.path.equals(rule.path);
        }

        public int hashCode() {
            return Objects.hash(this.path);
        }

        public static class RuleBuilder {
            @Nullable
            private String path;
            private Set<Capability> capabilities = new LinkedHashSet<Capability>();
            @Nullable
            private Duration minWrappingTtl;
            @Nullable
            private Duration maxWrappingTtl;
            private Map<String, List<String>> allowedParameters = new LinkedHashMap<String, List<String>>();
            private Map<String, List<String>> deniedParameters = new LinkedHashMap<String, List<String>>();

            public RuleBuilder path(String path) {
                Assert.hasText((String)path, (String)"Path must not be empty");
                this.path = path;
                return this;
            }

            public RuleBuilder capability(Capability capability) {
                Assert.notNull((Object)capability, (String)"Capability must not be null");
                this.capabilities.add(capability);
                return this;
            }

            public RuleBuilder capabilities(Capability ... capabilities) {
                Assert.notNull((Object)capabilities, (String)"Capabilities must not be null");
                Assert.noNullElements((Object[])capabilities, (String)"Capabilities must not contain null elements");
                return this.capabilities(Arrays.asList(capabilities));
            }

            public RuleBuilder capabilities(String ... capabilities) {
                Assert.notNull((Object)capabilities, (String)"Capabilities must not be null");
                Assert.noNullElements((Object[])capabilities, (String)"Capabilities must not contain null elements");
                List<Capability> mapped = Arrays.stream(capabilities).map(value -> {
                    Capability capability = BuiltinCapabilities.find(value);
                    if (capability == null) {
                        throw new IllegalArgumentException("Cannot resolve " + value + " to a capability");
                    }
                    return capability;
                }).collect(Collectors.toList());
                return this.capabilities(mapped);
            }

            private RuleBuilder capabilities(Iterable<Capability> capabilities) {
                for (Capability capability : capabilities) {
                    this.capabilities.add(capability);
                }
                return this;
            }

            public RuleBuilder minWrappingTtl(Duration ttl) {
                Assert.notNull((Object)ttl, (String)"TTL must not be null");
                this.minWrappingTtl = ttl;
                return this;
            }

            public RuleBuilder maxWrappingTtl(Duration ttl) {
                Assert.notNull((Object)ttl, (String)"TTL must not be null");
                this.maxWrappingTtl = ttl;
                return this;
            }

            public RuleBuilder allowedParameter(String name, String ... values) {
                Assert.hasText((String)name, (String)"Allowed parameter name must not be empty");
                Assert.notNull((Object)values, (String)"Values must not be null");
                this.allowedParameters.put(name, Arrays.asList(values));
                return this;
            }

            public RuleBuilder deniedParameter(String name, String ... values) {
                Assert.hasText((String)name, (String)"Denied parameter name must not be empty");
                Assert.notNull((Object)values, (String)"Values must not be null");
                this.deniedParameters.put(name, Arrays.asList(values));
                return this;
            }

            public Rule build() {
                List<Object> capabilities;
                Assert.state((boolean)StringUtils.hasText((String)this.path), (String)"Path must not be empty");
                Assert.state((!this.capabilities.isEmpty() ? 1 : 0) != 0, (String)"Rule must define one or more capabilities");
                switch (this.capabilities.size()) {
                    case 0: {
                        capabilities = Collections.emptyList();
                        break;
                    }
                    case 1: {
                        capabilities = Collections.singletonList(this.capabilities.iterator().next());
                        break;
                    }
                    default: {
                        capabilities = Collections.unmodifiableList(new ArrayList<Capability>(this.capabilities));
                    }
                }
                return new Rule(this.path, capabilities, this.minWrappingTtl, this.maxWrappingTtl, this.createMap(this.allowedParameters), this.createMap(this.deniedParameters));
            }

            private Map<String, List<String>> createMap(Map<String, List<String>> source) {
                if (source.isEmpty()) {
                    return Collections.emptyMap();
                }
                return Collections.unmodifiableMap(new LinkedHashMap<String, List<String>>(source));
            }
        }
    }
}

