/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class MacroParameterInstance {
    @JsonProperty
    private final String value;

    public static MacroParameterBuilder builder() {
        return new MacroParameterBuilder();
    }

    @JsonCreator
    private MacroParameterInstance() {
        this(MacroParameterInstance.builder());
    }

    private MacroParameterInstance(MacroParameterBuilder builder) {
        this.value = builder.value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroParameterInstance that = (MacroParameterInstance)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }

    public static class MacroParameterBuilder {
        private String value = null;

        private MacroParameterBuilder() {
        }

        public MacroParameterInstance build() {
            return new MacroParameterInstance(this);
        }

        public MacroParameterBuilder value(String value) {
            this.value = value;
            return this;
        }
    }
}

