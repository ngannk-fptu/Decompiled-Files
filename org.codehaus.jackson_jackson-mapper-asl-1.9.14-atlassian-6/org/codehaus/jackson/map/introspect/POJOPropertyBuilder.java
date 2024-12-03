/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.AnnotationMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class POJOPropertyBuilder
extends BeanPropertyDefinition
implements Comparable<POJOPropertyBuilder> {
    protected final String _name;
    protected final String _internalName;
    protected Node<AnnotatedField> _fields;
    protected Node<AnnotatedParameter> _ctorParameters;
    protected Node<AnnotatedMethod> _getters;
    protected Node<AnnotatedMethod> _setters;

    public POJOPropertyBuilder(String internalName) {
        this._internalName = internalName;
        this._name = internalName;
    }

    public POJOPropertyBuilder(POJOPropertyBuilder src, String newName) {
        this._internalName = src._internalName;
        this._name = newName;
        this._fields = src._fields;
        this._ctorParameters = src._ctorParameters;
        this._getters = src._getters;
        this._setters = src._setters;
    }

    public POJOPropertyBuilder withName(String newName) {
        return new POJOPropertyBuilder(this, newName);
    }

    @Override
    public int compareTo(POJOPropertyBuilder other) {
        if (this._ctorParameters != null) {
            if (other._ctorParameters == null) {
                return -1;
            }
        } else if (other._ctorParameters != null) {
            return 1;
        }
        return this.getName().compareTo(other.getName());
    }

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public String getInternalName() {
        return this._internalName;
    }

    @Override
    public boolean isExplicitlyIncluded() {
        return this.anyExplicitNames();
    }

    @Override
    public boolean hasGetter() {
        return this._getters != null;
    }

    @Override
    public boolean hasSetter() {
        return this._setters != null;
    }

    @Override
    public boolean hasField() {
        return this._fields != null;
    }

    @Override
    public boolean hasConstructorParameter() {
        return this._ctorParameters != null;
    }

    @Override
    public AnnotatedMember getAccessor() {
        AnnotatedMember m = this.getGetter();
        if (m == null) {
            m = this.getField();
        }
        return m;
    }

    @Override
    public AnnotatedMember getMutator() {
        AnnotatedMember m = this.getConstructorParameter();
        if (m == null && (m = this.getSetter()) == null) {
            m = this.getField();
        }
        return m;
    }

    @Override
    public boolean couldSerialize() {
        return this._getters != null || this._fields != null;
    }

    @Override
    public AnnotatedMethod getGetter() {
        if (this._getters == null) {
            return null;
        }
        AnnotatedMethod getter = (AnnotatedMethod)this._getters.value;
        Node next = this._getters.next;
        while (next != null) {
            block7: {
                AnnotatedMethod nextGetter;
                block5: {
                    Class<?> nextClass;
                    Class<?> getterClass;
                    block6: {
                        nextGetter = (AnnotatedMethod)next.value;
                        getterClass = getter.getDeclaringClass();
                        if (getterClass == (nextClass = nextGetter.getDeclaringClass())) break block5;
                        if (!getterClass.isAssignableFrom(nextClass)) break block6;
                        getter = nextGetter;
                        break block7;
                    }
                    if (nextClass.isAssignableFrom(getterClass)) break block7;
                }
                throw new IllegalArgumentException("Conflicting getter definitions for property \"" + this.getName() + "\": " + getter.getFullName() + " vs " + nextGetter.getFullName());
            }
            next = next.next;
        }
        return getter;
    }

    @Override
    public AnnotatedMethod getSetter() {
        if (this._setters == null) {
            return null;
        }
        AnnotatedMethod setter = (AnnotatedMethod)this._setters.value;
        Node next = this._setters.next;
        while (next != null) {
            block7: {
                AnnotatedMethod nextSetter;
                block5: {
                    Class<?> nextClass;
                    Class<?> setterClass;
                    block6: {
                        nextSetter = (AnnotatedMethod)next.value;
                        setterClass = setter.getDeclaringClass();
                        if (setterClass == (nextClass = nextSetter.getDeclaringClass())) break block5;
                        if (!setterClass.isAssignableFrom(nextClass)) break block6;
                        setter = nextSetter;
                        break block7;
                    }
                    if (nextClass.isAssignableFrom(setterClass)) break block7;
                }
                throw new IllegalArgumentException("Conflicting setter definitions for property \"" + this.getName() + "\": " + setter.getFullName() + " vs " + nextSetter.getFullName());
            }
            next = next.next;
        }
        return setter;
    }

    @Override
    public AnnotatedField getField() {
        if (this._fields == null) {
            return null;
        }
        AnnotatedField field = (AnnotatedField)this._fields.value;
        Node next = this._fields.next;
        while (next != null) {
            block7: {
                AnnotatedField nextField;
                block5: {
                    Class<?> nextClass;
                    Class<?> fieldClass;
                    block6: {
                        nextField = (AnnotatedField)next.value;
                        fieldClass = field.getDeclaringClass();
                        if (fieldClass == (nextClass = nextField.getDeclaringClass())) break block5;
                        if (!fieldClass.isAssignableFrom(nextClass)) break block6;
                        field = nextField;
                        break block7;
                    }
                    if (nextClass.isAssignableFrom(fieldClass)) break block7;
                }
                throw new IllegalArgumentException("Multiple fields representing property \"" + this.getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
            }
            next = next.next;
        }
        return field;
    }

    @Override
    public AnnotatedParameter getConstructorParameter() {
        if (this._ctorParameters == null) {
            return null;
        }
        Node<AnnotatedParameter> curr = this._ctorParameters;
        do {
            if (!(((AnnotatedParameter)curr.value).getOwner() instanceof AnnotatedConstructor)) continue;
            return (AnnotatedParameter)curr.value;
        } while ((curr = curr.next) != null);
        return (AnnotatedParameter)this._ctorParameters.value;
    }

    public void addField(AnnotatedField a, String ename, boolean visible, boolean ignored) {
        this._fields = new Node<AnnotatedField>(a, this._fields, ename, visible, ignored);
    }

    public void addCtor(AnnotatedParameter a, String ename, boolean visible, boolean ignored) {
        this._ctorParameters = new Node<AnnotatedParameter>(a, this._ctorParameters, ename, visible, ignored);
    }

    public void addGetter(AnnotatedMethod a, String ename, boolean visible, boolean ignored) {
        this._getters = new Node<AnnotatedMethod>(a, this._getters, ename, visible, ignored);
    }

    public void addSetter(AnnotatedMethod a, String ename, boolean visible, boolean ignored) {
        this._setters = new Node<AnnotatedMethod>(a, this._setters, ename, visible, ignored);
    }

    public void addAll(POJOPropertyBuilder src) {
        this._fields = POJOPropertyBuilder.merge(this._fields, src._fields);
        this._ctorParameters = POJOPropertyBuilder.merge(this._ctorParameters, src._ctorParameters);
        this._getters = POJOPropertyBuilder.merge(this._getters, src._getters);
        this._setters = POJOPropertyBuilder.merge(this._setters, src._setters);
    }

    private static <T> Node<T> merge(Node<T> chain1, Node<T> chain2) {
        if (chain1 == null) {
            return chain2;
        }
        if (chain2 == null) {
            return chain1;
        }
        return ((Node)chain1).append((Node)chain2);
    }

    public void removeIgnored() {
        this._fields = this._removeIgnored(this._fields);
        this._getters = this._removeIgnored(this._getters);
        this._setters = this._removeIgnored(this._setters);
        this._ctorParameters = this._removeIgnored(this._ctorParameters);
    }

    public void removeNonVisible() {
        this._getters = this._removeNonVisible(this._getters);
        this._ctorParameters = this._removeNonVisible(this._ctorParameters);
        if (this._getters == null) {
            this._fields = this._removeNonVisible(this._fields);
            this._setters = this._removeNonVisible(this._setters);
        }
    }

    public void trimByVisibility() {
        this._fields = this._trimByVisibility(this._fields);
        this._getters = this._trimByVisibility(this._getters);
        this._setters = this._trimByVisibility(this._setters);
        this._ctorParameters = this._trimByVisibility(this._ctorParameters);
    }

    public void mergeAnnotations(boolean forSerialization) {
        if (forSerialization) {
            if (this._getters != null) {
                AnnotationMap ann = this._mergeAnnotations(0, this._getters, this._fields, this._ctorParameters, this._setters);
                this._getters = this._getters.withValue(((AnnotatedMethod)this._getters.value).withAnnotations(ann));
            } else if (this._fields != null) {
                AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._ctorParameters, this._setters);
                this._fields = this._fields.withValue(((AnnotatedField)this._fields.value).withAnnotations(ann));
            }
        } else if (this._ctorParameters != null) {
            AnnotationMap ann = this._mergeAnnotations(0, this._ctorParameters, this._setters, this._fields, this._getters);
            this._ctorParameters = this._ctorParameters.withValue(((AnnotatedParameter)this._ctorParameters.value).withAnnotations(ann));
        } else if (this._setters != null) {
            AnnotationMap ann = this._mergeAnnotations(0, this._setters, this._fields, this._getters);
            this._setters = this._setters.withValue(((AnnotatedMethod)this._setters.value).withAnnotations(ann));
        } else if (this._fields != null) {
            AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._getters);
            this._fields = this._fields.withValue(((AnnotatedField)this._fields.value).withAnnotations(ann));
        }
    }

    private AnnotationMap _mergeAnnotations(int index, Node<? extends AnnotatedMember> ... nodes) {
        AnnotationMap ann = ((AnnotatedMember)nodes[index].value).getAllAnnotations();
        ++index;
        while (index < nodes.length) {
            if (nodes[index] != null) {
                return AnnotationMap.merge(ann, this._mergeAnnotations(index, nodes));
            }
            ++index;
        }
        return ann;
    }

    private <T> Node<T> _removeIgnored(Node<T> node) {
        if (node == null) {
            return node;
        }
        return node.withoutIgnored();
    }

    private <T> Node<T> _removeNonVisible(Node<T> node) {
        if (node == null) {
            return node;
        }
        return node.withoutNonVisible();
    }

    private <T> Node<T> _trimByVisibility(Node<T> node) {
        if (node == null) {
            return node;
        }
        return node.trimByVisibility();
    }

    public boolean anyExplicitNames() {
        return this._anyExplicitNames(this._fields) || this._anyExplicitNames(this._getters) || this._anyExplicitNames(this._setters) || this._anyExplicitNames(this._ctorParameters);
    }

    private <T> boolean _anyExplicitNames(Node<T> n) {
        while (n != null) {
            if (n.explicitName != null && n.explicitName.length() > 0) {
                return true;
            }
            n = n.next;
        }
        return false;
    }

    public boolean anyVisible() {
        return this._anyVisible(this._fields) || this._anyVisible(this._getters) || this._anyVisible(this._setters) || this._anyVisible(this._ctorParameters);
    }

    private <T> boolean _anyVisible(Node<T> n) {
        while (n != null) {
            if (n.isVisible) {
                return true;
            }
            n = n.next;
        }
        return false;
    }

    public boolean anyIgnorals() {
        return this._anyIgnorals(this._fields) || this._anyIgnorals(this._getters) || this._anyIgnorals(this._setters) || this._anyIgnorals(this._ctorParameters);
    }

    public boolean anyDeserializeIgnorals() {
        return this._anyIgnorals(this._fields) || this._anyIgnorals(this._setters) || this._anyIgnorals(this._ctorParameters);
    }

    public boolean anySerializeIgnorals() {
        return this._anyIgnorals(this._fields) || this._anyIgnorals(this._getters);
    }

    private <T> boolean _anyIgnorals(Node<T> n) {
        while (n != null) {
            if (n.isMarkedIgnored) {
                return true;
            }
            n = n.next;
        }
        return false;
    }

    public String findNewName() {
        Node<? extends AnnotatedMember> renamed = null;
        renamed = this.findRenamed(this._fields, renamed);
        renamed = this.findRenamed(this._getters, renamed);
        renamed = this.findRenamed(this._setters, renamed);
        return (renamed = this.findRenamed(this._ctorParameters, renamed)) == null ? null : renamed.explicitName;
    }

    private Node<? extends AnnotatedMember> findRenamed(Node<? extends AnnotatedMember> node, Node<? extends AnnotatedMember> renamed) {
        while (node != null) {
            String explName = node.explicitName;
            if (explName != null && !explName.equals(this._name)) {
                if (renamed == null) {
                    renamed = node;
                } else if (!explName.equals(renamed.explicitName)) {
                    throw new IllegalStateException("Conflicting property name definitions: '" + renamed.explicitName + "' (for " + renamed.value + ") vs '" + node.explicitName + "' (for " + node.value + ")");
                }
            }
            node = node.next;
        }
        return renamed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Property '").append(this._name).append("'; ctors: ").append(this._ctorParameters).append(", field(s): ").append(this._fields).append(", getter(s): ").append(this._getters).append(", setter(s): ").append(this._setters);
        sb.append("]");
        return sb.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Node<T> {
        public final T value;
        public final Node<T> next;
        public final String explicitName;
        public final boolean isVisible;
        public final boolean isMarkedIgnored;

        public Node(T v, Node<T> n, String explName, boolean visible, boolean ignored) {
            this.value = v;
            this.next = n;
            this.explicitName = explName == null ? null : (explName.length() == 0 ? null : explName);
            this.isVisible = visible;
            this.isMarkedIgnored = ignored;
        }

        public Node<T> withValue(T newValue) {
            if (newValue == this.value) {
                return this;
            }
            return new Node<T>(newValue, this.next, this.explicitName, this.isVisible, this.isMarkedIgnored);
        }

        public Node<T> withNext(Node<T> newNext) {
            if (newNext == this.next) {
                return this;
            }
            return new Node<T>(this.value, newNext, this.explicitName, this.isVisible, this.isMarkedIgnored);
        }

        public Node<T> withoutIgnored() {
            Node<T> newNext;
            if (this.isMarkedIgnored) {
                return this.next == null ? null : this.next.withoutIgnored();
            }
            if (this.next != null && (newNext = this.next.withoutIgnored()) != this.next) {
                return this.withNext(newNext);
            }
            return this;
        }

        public Node<T> withoutNonVisible() {
            Node<T> newNext = this.next == null ? null : this.next.withoutNonVisible();
            return this.isVisible ? this.withNext(newNext) : newNext;
        }

        private Node<T> append(Node<T> appendable) {
            if (this.next == null) {
                return this.withNext(appendable);
            }
            return this.withNext(super.append(appendable));
        }

        public Node<T> trimByVisibility() {
            if (this.next == null) {
                return this;
            }
            Node<T> newNext = this.next.trimByVisibility();
            if (this.explicitName != null) {
                if (newNext.explicitName == null) {
                    return this.withNext(null);
                }
                return this.withNext(newNext);
            }
            if (newNext.explicitName != null) {
                return newNext;
            }
            if (this.isVisible == newNext.isVisible) {
                return this.withNext(newNext);
            }
            return this.isVisible ? this.withNext(null) : newNext;
        }

        public String toString() {
            String msg = this.value.toString() + "[visible=" + this.isVisible + "]";
            if (this.next != null) {
                msg = msg + ", " + this.next.toString();
            }
            return msg;
        }
    }
}

