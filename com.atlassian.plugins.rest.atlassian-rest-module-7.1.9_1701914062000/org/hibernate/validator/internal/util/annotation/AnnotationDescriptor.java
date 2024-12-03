/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationFactory;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttributes;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;

public class AnnotationDescriptor<A extends Annotation>
implements Serializable {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<A> type;
    private final Map<String, Object> attributes;
    private final int hashCode;
    private final A annotation;

    public AnnotationDescriptor(A annotation) {
        this.type = annotation.annotationType();
        this.attributes = AnnotationDescriptor.run(GetAnnotationAttributes.action(annotation));
        this.annotation = annotation;
        this.hashCode = this.buildHashCode();
    }

    public AnnotationDescriptor(AnnotationDescriptor<A> descriptor) {
        this.type = descriptor.type;
        this.attributes = descriptor.attributes;
        this.hashCode = descriptor.hashCode;
        this.annotation = descriptor.annotation;
    }

    private AnnotationDescriptor(Class<A> annotationType, Map<String, Object> attributes) {
        this.type = annotationType;
        this.attributes = CollectionHelper.toImmutableMap(attributes);
        this.hashCode = this.buildHashCode();
        this.annotation = AnnotationFactory.create(this);
    }

    public Class<A> getType() {
        return this.type;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public <T> T getMandatoryAttribute(String attributeName, Class<T> attributeType) {
        Object attribute = this.attributes.get(attributeName);
        if (attribute == null) {
            throw LOG.getUnableToFindAnnotationAttributeException(this.type, attributeName, null);
        }
        if (!attributeType.isAssignableFrom(attribute.getClass())) {
            throw LOG.getWrongAnnotationAttributeTypeException(this.type, attributeName, attributeType, attribute.getClass());
        }
        return (T)attribute;
    }

    public <T> T getAttribute(String attributeName, Class<T> attributeType) {
        Object attribute = this.attributes.get(attributeName);
        if (attribute == null) {
            return null;
        }
        if (!attributeType.isAssignableFrom(attribute.getClass())) {
            throw LOG.getWrongAnnotationAttributeTypeException(this.type, attributeName, attributeType, attribute.getClass());
        }
        return (T)attribute;
    }

    public Object getAttribute(String attributeName) {
        return this.attributes.get(attributeName);
    }

    public A getAnnotation() {
        return this.annotation;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof AnnotationDescriptor)) {
            return false;
        }
        AnnotationDescriptor other = (AnnotationDescriptor)obj;
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.attributes.size() != other.attributes.size()) {
            return false;
        }
        for (Map.Entry<String, Object> member : this.attributes.entrySet()) {
            Object otherValue;
            Object value = member.getValue();
            if (this.areEqual(value, otherValue = other.attributes.get(member.getKey()))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(StringHelper.toShortString(this.type)).append('(');
        for (String s : this.getRegisteredAttributesInAlphabeticalOrder()) {
            result.append(s).append('=').append(this.attributes.get(s)).append(", ");
        }
        if (this.attributes.size() > 0) {
            result.delete(result.length() - 2, result.length());
            result.append(")");
        } else {
            result.delete(result.length() - 1, result.length());
        }
        return result.toString();
    }

    private SortedSet<String> getRegisteredAttributesInAlphabeticalOrder() {
        return new TreeSet<String>(this.attributes.keySet());
    }

    private int buildHashCode() {
        int hashCode = 0;
        for (Map.Entry<String, Object> member : this.attributes.entrySet()) {
            Object value = member.getValue();
            int nameHashCode = member.getKey().hashCode();
            int valueHashCode = !value.getClass().isArray() ? value.hashCode() : (value.getClass() == boolean[].class ? Arrays.hashCode((boolean[])value) : (value.getClass() == byte[].class ? Arrays.hashCode((byte[])value) : (value.getClass() == char[].class ? Arrays.hashCode((char[])value) : (value.getClass() == double[].class ? Arrays.hashCode((double[])value) : (value.getClass() == float[].class ? Arrays.hashCode((float[])value) : (value.getClass() == int[].class ? Arrays.hashCode((int[])value) : (value.getClass() == long[].class ? Arrays.hashCode((long[])value) : (value.getClass() == short[].class ? Arrays.hashCode((short[])value) : Arrays.hashCode((Object[])value)))))))));
            hashCode += 127 * nameHashCode ^ valueHashCode;
        }
        return hashCode;
    }

    private boolean areEqual(Object o1, Object o2) {
        return !o1.getClass().isArray() ? o1.equals(o2) : (o1.getClass() == boolean[].class ? Arrays.equals((boolean[])o1, (boolean[])o2) : (o1.getClass() == byte[].class ? Arrays.equals((byte[])o1, (byte[])o2) : (o1.getClass() == char[].class ? Arrays.equals((char[])o1, (char[])o2) : (o1.getClass() == double[].class ? Arrays.equals((double[])o1, (double[])o2) : (o1.getClass() == float[].class ? Arrays.equals((float[])o1, (float[])o2) : (o1.getClass() == int[].class ? Arrays.equals((int[])o1, (int[])o2) : (o1.getClass() == long[].class ? Arrays.equals((long[])o1, (long[])o2) : (o1.getClass() == short[].class ? Arrays.equals((short[])o1, (short[])o2) : Arrays.equals((Object[])o1, (Object[])o2)))))))));
    }

    private static <V> V run(PrivilegedAction<V> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    public static class Builder<S extends Annotation> {
        private final Class<S> type;
        private final Map<String, Object> attributes;

        public Builder(Class<S> type) {
            this.type = type;
            this.attributes = new HashMap<String, Object>();
        }

        public Builder(Class<S> type, Map<String, Object> attributes) {
            this.type = type;
            this.attributes = new HashMap<String, Object>(attributes);
        }

        public Builder(S annotation) {
            this.type = annotation.annotationType();
            this.attributes = new HashMap<String, Object>((Map)AnnotationDescriptor.run(GetAnnotationAttributes.action(annotation)));
        }

        public void setAttribute(String attributeName, Object value) {
            this.attributes.put(attributeName, value);
        }

        public boolean hasAttribute(String key) {
            return this.attributes.containsKey(key);
        }

        public Class<S> getType() {
            return this.type;
        }

        public AnnotationDescriptor<S> build() {
            return new AnnotationDescriptor(this.type, this.getAnnotationAttributes());
        }

        private Map<String, Object> getAnnotationAttributes() {
            Method[] declaredMethods;
            HashMap<String, Object> result = CollectionHelper.newHashMap(this.attributes.size());
            int processedValuesFromDescriptor = 0;
            for (Method m : declaredMethods = (Method[])AnnotationDescriptor.run(GetDeclaredMethods.action(this.type))) {
                Object elementValue = this.attributes.get(m.getName());
                if (elementValue != null) {
                    result.put(m.getName(), elementValue);
                    ++processedValuesFromDescriptor;
                    continue;
                }
                if (m.getDefaultValue() != null) {
                    result.put(m.getName(), m.getDefaultValue());
                    continue;
                }
                throw LOG.getNoValueProvidedForAnnotationAttributeException(m.getName(), this.type);
            }
            if (processedValuesFromDescriptor != this.attributes.size()) {
                Set<String> unknownAttributes = this.attributes.keySet();
                unknownAttributes.removeAll(result.keySet());
                throw LOG.getTryingToInstantiateAnnotationWithUnknownAttributesException(this.type, unknownAttributes);
            }
            return result;
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append('@').append(StringHelper.toShortString(this.type)).append('(');
            for (String s : this.getRegisteredAttributesInAlphabeticalOrder()) {
                result.append(s).append('=').append(this.attributes.get(s)).append(", ");
            }
            if (this.attributes.size() > 0) {
                result.delete(result.length() - 2, result.length());
                result.append(")");
            } else {
                result.delete(result.length() - 1, result.length());
            }
            return result.toString();
        }

        private SortedSet<String> getRegisteredAttributesInAlphabeticalOrder() {
            return new TreeSet<String>(this.attributes.keySet());
        }
    }
}

