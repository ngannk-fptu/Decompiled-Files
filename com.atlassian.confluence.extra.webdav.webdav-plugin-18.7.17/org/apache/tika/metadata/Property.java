/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.metadata.PropertyTypeException;

public final class Property
implements Comparable<Property> {
    private static final Map<String, Property> PROPERTIES = new ConcurrentHashMap<String, Property>();
    private final String name;
    private final boolean internal;
    private final PropertyType propertyType;
    private final ValueType valueType;
    private final Property primaryProperty;
    private final Property[] secondaryExtractProperties;
    private final Set<String> choices;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType, String[] choices, Property primaryProperty, Property[] secondaryExtractProperties) {
        this.name = name;
        this.internal = internal;
        this.propertyType = propertyType;
        this.valueType = valueType;
        this.choices = choices != null ? Collections.unmodifiableSet(new HashSet<Object>(Arrays.asList((Object[])choices.clone()))) : null;
        if (primaryProperty != null) {
            this.primaryProperty = primaryProperty;
            this.secondaryExtractProperties = secondaryExtractProperties;
        } else {
            this.primaryProperty = this;
            this.secondaryExtractProperties = null;
            Map<String, Property> map = PROPERTIES;
            synchronized (map) {
                PROPERTIES.put(name, this);
            }
        }
    }

    private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType, String[] choices) {
        this(name, internal, propertyType, valueType, choices, null, null);
    }

    private Property(String name, boolean internal, ValueType valueType, String[] choices) {
        this(name, internal, PropertyType.SIMPLE, valueType, choices);
    }

    private Property(String name, boolean internal, ValueType valueType) {
        this(name, internal, PropertyType.SIMPLE, valueType, null);
    }

    private Property(String name, boolean internal, PropertyType propertyType, ValueType valueType) {
        this(name, internal, propertyType, valueType, null);
    }

    public static PropertyType getPropertyType(String key) {
        PropertyType type = null;
        Property prop = PROPERTIES.get(key);
        if (prop != null) {
            type = prop.getPropertyType();
        }
        return type;
    }

    public static Property get(String key) {
        return PROPERTIES.get(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SortedSet<Property> getProperties(String prefix) {
        TreeSet<Property> set = new TreeSet<Property>();
        String p = prefix + ":";
        Map<String, Property> map = PROPERTIES;
        synchronized (map) {
            for (Map.Entry<String, Property> entry : PROPERTIES.entrySet()) {
                if (!entry.getKey().startsWith(p)) continue;
                set.add(entry.getValue());
            }
        }
        return set;
    }

    public static Property internalBoolean(String name) {
        return new Property(name, true, ValueType.BOOLEAN);
    }

    public static Property internalClosedChoise(String name, String ... choices) {
        return new Property(name, true, ValueType.CLOSED_CHOICE, choices);
    }

    public static Property internalDate(String name) {
        return new Property(name, true, ValueType.DATE);
    }

    public static Property internalInteger(String name) {
        return new Property(name, true, ValueType.INTEGER);
    }

    public static Property internalIntegerSequence(String name) {
        return new Property(name, true, PropertyType.SEQ, ValueType.INTEGER);
    }

    public static Property internalRational(String name) {
        return new Property(name, true, ValueType.RATIONAL);
    }

    public static Property internalOpenChoise(String name, String ... choices) {
        return new Property(name, true, ValueType.OPEN_CHOICE, choices);
    }

    public static Property internalReal(String name) {
        return new Property(name, true, ValueType.REAL);
    }

    public static Property internalText(String name) {
        return new Property(name, true, ValueType.TEXT);
    }

    public static Property internalTextBag(String name) {
        return new Property(name, true, PropertyType.BAG, ValueType.TEXT);
    }

    public static Property internalURI(String name) {
        return new Property(name, true, ValueType.URI);
    }

    public static Property externalClosedChoise(String name, String ... choices) {
        return new Property(name, false, ValueType.CLOSED_CHOICE, choices);
    }

    public static Property externalOpenChoise(String name, String ... choices) {
        return new Property(name, false, ValueType.OPEN_CHOICE, choices);
    }

    public static Property externalDate(String name) {
        return new Property(name, false, ValueType.DATE);
    }

    public static Property externalReal(String name) {
        return new Property(name, false, ValueType.REAL);
    }

    public static Property externalRealSeq(String name) {
        return new Property(name, false, PropertyType.SEQ, ValueType.REAL);
    }

    public static Property externalInteger(String name) {
        return new Property(name, false, ValueType.INTEGER);
    }

    public static Property externalBoolean(String name) {
        return new Property(name, false, ValueType.BOOLEAN);
    }

    public static Property externalBooleanSeq(String name) {
        return new Property(name, false, PropertyType.SEQ, ValueType.BOOLEAN);
    }

    public static Property externalText(String name) {
        return new Property(name, false, ValueType.TEXT);
    }

    public static Property externalTextBag(String name) {
        return new Property(name, false, PropertyType.BAG, ValueType.TEXT);
    }

    public static Property composite(Property primaryProperty, Property[] secondaryExtractProperties) {
        if (primaryProperty == null) {
            throw new NullPointerException("primaryProperty must not be null");
        }
        if (primaryProperty.getPropertyType() == PropertyType.COMPOSITE) {
            throw new PropertyTypeException(primaryProperty.getPropertyType());
        }
        if (secondaryExtractProperties != null) {
            for (Property secondaryExtractProperty : secondaryExtractProperties) {
                if (secondaryExtractProperty.getPropertyType() != PropertyType.COMPOSITE) continue;
                throw new PropertyTypeException(secondaryExtractProperty.getPropertyType());
            }
        }
        String[] choices = null;
        if (primaryProperty.getChoices() != null) {
            choices = primaryProperty.getChoices().toArray(new String[0]);
        }
        return new Property(primaryProperty.getName(), primaryProperty.isInternal(), PropertyType.COMPOSITE, ValueType.PROPERTY, choices, primaryProperty, secondaryExtractProperties);
    }

    public String getName() {
        return this.name;
    }

    public boolean isInternal() {
        return this.internal;
    }

    public boolean isExternal() {
        return !this.internal;
    }

    public boolean isMultiValuePermitted() {
        if (this.propertyType == PropertyType.BAG || this.propertyType == PropertyType.SEQ || this.propertyType == PropertyType.ALT) {
            return true;
        }
        if (this.propertyType == PropertyType.COMPOSITE) {
            return this.primaryProperty.isMultiValuePermitted();
        }
        return false;
    }

    public PropertyType getPropertyType() {
        return this.propertyType;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public Set<String> getChoices() {
        return this.choices;
    }

    public Property getPrimaryProperty() {
        return this.primaryProperty;
    }

    public Property[] getSecondaryExtractProperties() {
        return this.secondaryExtractProperties;
    }

    @Override
    public int compareTo(Property o) {
        return this.name.compareTo(o.name);
    }

    public boolean equals(Object o) {
        return o instanceof Property && this.name.equals(((Property)o).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public static enum ValueType {
        BOOLEAN,
        OPEN_CHOICE,
        CLOSED_CHOICE,
        DATE,
        INTEGER,
        LOCALE,
        MIME_TYPE,
        PROPER_NAME,
        RATIONAL,
        REAL,
        TEXT,
        URI,
        URL,
        XPATH,
        PROPERTY;

    }

    public static enum PropertyType {
        SIMPLE,
        STRUCTURE,
        BAG,
        SEQ,
        ALT,
        COMPOSITE;

    }
}

