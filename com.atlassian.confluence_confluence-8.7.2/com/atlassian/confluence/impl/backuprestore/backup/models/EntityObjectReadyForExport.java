/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.models;

import com.atlassian.confluence.security.InvalidOperationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EntityObjectReadyForExport {
    private final List<Property> ids;
    private final Class<?> clazz;
    private final Collection<Property> properties = new ArrayList<Property>();
    private final Collection<Reference> references = new ArrayList<Reference>();
    private final Collection<CollectionOfElements> collections = new ArrayList<CollectionOfElements>();

    public EntityObjectReadyForExport(Property id, Class<?> clazz) {
        this.ids = Arrays.asList(id);
        this.clazz = clazz;
    }

    public EntityObjectReadyForExport(List<Property> ids, Class<?> clazz) {
        this.ids = ids;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Property getId() {
        if (this.ids.size() != 1) {
            throw new InvalidOperationException("Requested single column key but the entity " + this.getClazz().toString() + " has more than 1 key column (ot the key is missing).");
        }
        return this.ids.get(0);
    }

    public List<Property> getIds() {
        return this.ids;
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }

    public void addReference(Reference reference) {
        this.references.add(reference);
    }

    public void addCollectionOfElements(CollectionOfElements collectionOfElements) {
        this.collections.add(collectionOfElements);
    }

    public Collection<Property> getProperties() {
        return this.properties;
    }

    public Property getProperty(String name) {
        return this.properties.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
    }

    public Collection<Reference> getReferences() {
        return this.references;
    }

    public Collection<CollectionOfElements> getCollections() {
        return this.collections;
    }

    public Optional<Reference> findReferenceByName(String name) {
        return this.references.stream().filter(r -> r.getPropertyName().equals(name)).findFirst();
    }

    public String getReason() {
        return null;
    }

    public Optional<Property> findPropertyByName(String name) {
        return this.properties.stream().filter(r -> r.getName().equals(name)).findFirst();
    }

    public static class CollectionOfElements {
        private final String collectionName;
        private final Class<?> collectionClazz;
        private final Class<?> referencedClazz;
        private final Collection<Object> elementValues;
        private final Map<Object, Object> map;

        public CollectionOfElements(String collectionName, Class<?> collectionClazz, Class<?> referencedClazz, Collection<Object> elementValues) {
            this(collectionName, collectionClazz, referencedClazz, elementValues, Collections.emptyMap());
        }

        public CollectionOfElements(String collectionName, Map<Object, Object> map) {
            this(collectionName, Map.class, null, Collections.emptySet(), map);
        }

        private CollectionOfElements(String collectionName, Class<?> collectionClazz, Class<?> referencedClazz, Collection<Object> elementValues, Map<Object, Object> map) {
            this.collectionName = collectionName;
            this.collectionClazz = collectionClazz;
            this.referencedClazz = referencedClazz;
            this.elementValues = elementValues;
            this.map = map;
        }

        public String getCollectionName() {
            return this.collectionName;
        }

        public Class<?> getCollectionClazz() {
            return this.collectionClazz;
        }

        public Class<?> getReferencedClazz() {
            return this.referencedClazz;
        }

        public Collection<Object> getElementValues() {
            return this.elementValues;
        }

        public Map<Object, Object> getMap() {
            return this.map;
        }

        public boolean isEmpty() {
            return Map.class.equals(this.collectionClazz) ? this.map.isEmpty() : this.elementValues.isEmpty();
        }
    }

    public static class Reference {
        private final String propertyName;
        private final Class<?> referencedClazz;
        private final Property referencedId;

        public Reference(String propertyName, Class<?> referencedClazz, Property referencedId) {
            this.propertyName = propertyName;
            this.referencedClazz = referencedClazz;
            this.referencedId = referencedId;
        }

        public String getPropertyName() {
            return this.propertyName;
        }

        public Class<?> getReferencedClazz() {
            return this.referencedClazz;
        }

        public Property getReferencedId() {
            return this.referencedId;
        }

        public String toString() {
            return "Reference{propertyName='" + this.propertyName + "', referencedClazz=" + this.referencedClazz + ", referencedId=" + this.referencedId + "}";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Reference reference = (Reference)o;
            return Objects.equals(this.propertyName, reference.propertyName) && Objects.equals(this.referencedClazz, reference.referencedClazz) && Objects.equals(this.referencedId, reference.referencedId);
        }

        public int hashCode() {
            return Objects.hash(this.propertyName, this.referencedClazz, this.referencedId);
        }
    }

    public static class Property {
        private final String name;
        private final Object value;
        private final Class<?> clazz;

        public Property(String name, Object value) {
            this.clazz = null;
            this.name = name;
            this.value = value;
        }

        public Property(Class<?> clazz, String name, Object value) {
            this.clazz = clazz;
            this.name = name;
            this.value = value;
        }

        public String getStringValue() {
            return (String)this.value;
        }

        public Long getLongValue() {
            return (Long)this.value;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public String getName() {
            return this.name;
        }

        public Object getValue() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Property property = (Property)o;
            return Objects.equals(this.clazz, property.clazz) && Objects.equals(this.name, property.name) && Objects.equals(this.value, property.value);
        }

        public int hashCode() {
            return Objects.hash(this.clazz, this.name, this.value);
        }

        public String toString() {
            return "Property{name='" + this.name + "', value=" + this.value + "}";
        }
    }
}

