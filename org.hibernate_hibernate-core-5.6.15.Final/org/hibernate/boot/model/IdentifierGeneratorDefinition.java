/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.internal.util.collections.CollectionHelper;

public class IdentifierGeneratorDefinition
implements Serializable {
    private final String name;
    private final String strategy;
    private final Map<String, String> parameters;

    public IdentifierGeneratorDefinition(String name, String strategy, Map<String, String> parameters) {
        this.name = name;
        this.strategy = strategy;
        this.parameters = CollectionHelper.isEmpty(parameters) ? Collections.emptyMap() : Collections.unmodifiableMap(parameters);
    }

    public IdentifierGeneratorDefinition(String name, Map<String, String> parameters) {
        this(name, name, parameters);
    }

    public IdentifierGeneratorDefinition(String name) {
        this(name, name);
    }

    public IdentifierGeneratorDefinition(String name, String strategy) {
        this.name = name;
        this.strategy = strategy;
        this.parameters = Collections.emptyMap();
    }

    public String getStrategy() {
        return this.strategy;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifierGeneratorDefinition)) {
            return false;
        }
        IdentifierGeneratorDefinition that = (IdentifierGeneratorDefinition)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.parameters != null ? !this.parameters.equals(that.parameters) : that.parameters != null) {
            return false;
        }
        return !(this.strategy != null ? !this.strategy.equals(that.strategy) : that.strategy != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.strategy != null ? this.strategy.hashCode() : 0);
        result = 31 * result + (this.parameters != null ? this.parameters.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "IdentifierGeneratorDefinition{name='" + this.name + '\'' + ", strategy='" + this.strategy + '\'' + ", parameters=" + this.parameters + '}';
    }

    public static class Builder {
        private String name;
        private String strategy;
        private Map<String, String> parameters;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStrategy() {
            return this.strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }

        public void addParam(String name, String value) {
            this.parameters().put(name, value);
        }

        private Map<String, String> parameters() {
            if (this.parameters == null) {
                this.parameters = new HashMap<String, String>();
            }
            return this.parameters;
        }

        public void addParams(Map<String, String> parameters) {
            this.parameters().putAll(parameters);
        }

        public IdentifierGeneratorDefinition build() {
            return new IdentifierGeneratorDefinition(this.name, this.strategy, this.parameters);
        }
    }
}

