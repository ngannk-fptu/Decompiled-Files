/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.confluence.api.model.content.MacroParameterInstance;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class MacroInstance {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String body;
    @JsonProperty
    private final Map<String, MacroParameterInstance> parameters;

    public static MacroBuilder builder() {
        return new MacroBuilder();
    }

    @JsonCreator
    private MacroInstance() {
        this(MacroInstance.builder());
    }

    private MacroInstance(MacroBuilder builder) {
        this.name = builder.name;
        this.body = builder.body;
        this.parameters = Collections.unmodifiableMap(builder.parameters);
    }

    public String getName() {
        return this.name;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, MacroParameterInstance> getParameters() {
        return this.parameters;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroInstance that = (MacroInstance)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.body, that.body) && Objects.equals(this.parameters, that.parameters);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.body, this.parameters);
    }

    public static class MacroBuilder {
        private String name = null;
        private String body = null;
        private final Map<String, MacroParameterInstance> parameters = new HashMap<String, MacroParameterInstance>();

        private MacroBuilder() {
        }

        public MacroInstance build() {
            return new MacroInstance(this);
        }

        public MacroBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MacroBuilder body(String body) {
            this.body = body;
            return this;
        }

        public MacroBuilder parameters(Map<String, String> parameters) {
            this.parameters.putAll(parameters.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> MacroParameterInstance.builder().value((String)entry.getValue()).build())));
            return this;
        }

        public MacroBuilder addParameter(String key, String value) {
            this.parameters.put(key, MacroParameterInstance.builder().value(value).build());
            return this;
        }
    }
}

