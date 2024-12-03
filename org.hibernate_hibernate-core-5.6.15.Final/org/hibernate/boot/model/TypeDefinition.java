/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class TypeDefinition
implements Serializable {
    private final String name;
    private final Class typeImplementorClass;
    private final String[] registrationKeys;
    private final Map<String, String> parameters;

    public TypeDefinition(String name, Class typeImplementorClass, String[] registrationKeys, Map<String, String> parameters) {
        this.name = name;
        this.typeImplementorClass = typeImplementorClass;
        this.registrationKeys = registrationKeys;
        this.parameters = parameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(parameters);
    }

    public TypeDefinition(String name, Class typeImplementorClass, String[] registrationKeys, Properties parameters) {
        this.name = name;
        this.typeImplementorClass = typeImplementorClass;
        this.registrationKeys = registrationKeys;
        this.parameters = parameters == null ? Collections.emptyMap() : this.extractStrings(parameters);
    }

    private Map<String, String> extractStrings(Properties properties) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (!String.class.isInstance(entry.getKey()) || !String.class.isInstance(entry.getValue())) continue;
            parameters.put((String)entry.getKey(), (String)entry.getValue());
        }
        return parameters;
    }

    public String getName() {
        return this.name;
    }

    public Class getTypeImplementorClass() {
        return this.typeImplementorClass;
    }

    public String[] getRegistrationKeys() {
        return this.registrationKeys;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Properties getParametersAsProperties() {
        Properties properties = new Properties();
        properties.putAll(this.parameters);
        return properties;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeDefinition)) {
            return false;
        }
        TypeDefinition that = (TypeDefinition)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.typeImplementorClass, that.typeImplementorClass) && Arrays.equals(this.registrationKeys, that.registrationKeys) && Objects.equals(this.parameters, that.parameters);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.typeImplementorClass != null ? this.typeImplementorClass.hashCode() : 0);
        result = 31 * result + (this.registrationKeys != null ? Arrays.hashCode(this.registrationKeys) : 0);
        result = 31 * result + (this.parameters != null ? this.parameters.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "TypeDefinition{name='" + this.name + '\'' + ", typeImplementorClass=" + this.typeImplementorClass + ", registrationKeys=" + Arrays.toString(this.registrationKeys) + ", parameters=" + this.parameters + '}';
    }
}

