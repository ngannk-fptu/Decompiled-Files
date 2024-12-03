/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.Path$BeanNode
 *  javax.validation.Path$ConstructorNode
 *  javax.validation.Path$ContainerElementNode
 *  javax.validation.Path$CrossParameterNode
 *  javax.validation.Path$MethodNode
 *  javax.validation.Path$Node
 *  javax.validation.Path$ParameterNode
 *  javax.validation.Path$PropertyNode
 *  javax.validation.Path$ReturnValueNode
 */
package org.hibernate.validator.internal.engine.path;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.validation.ElementKind;
import javax.validation.Path;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.path.ContainerElementNode;
import org.hibernate.validator.path.PropertyNode;

public class NodeImpl
implements Path.PropertyNode,
Path.MethodNode,
Path.ConstructorNode,
Path.BeanNode,
Path.ParameterNode,
Path.ReturnValueNode,
Path.CrossParameterNode,
Path.ContainerElementNode,
PropertyNode,
ContainerElementNode,
Serializable {
    private static final long serialVersionUID = 2075466571633860499L;
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String INDEX_OPEN = "[";
    private static final String INDEX_CLOSE = "]";
    private static final String TYPE_PARAMETER_OPEN = "<";
    private static final String TYPE_PARAMETER_CLOSE = ">";
    public static final String RETURN_VALUE_NODE_NAME = "<return value>";
    public static final String CROSS_PARAMETER_NODE_NAME = "<cross-parameter>";
    public static final String ITERABLE_ELEMENT_NODE_NAME = "<iterable element>";
    public static final String LIST_ELEMENT_NODE_NAME = "<list element>";
    public static final String MAP_KEY_NODE_NAME = "<map key>";
    public static final String MAP_VALUE_NODE_NAME = "<map value>";
    private final String name;
    private final NodeImpl parent;
    private final boolean isIterable;
    private final Integer index;
    private final Object key;
    private final ElementKind kind;
    private final Class<?>[] parameterTypes;
    private final Integer parameterIndex;
    private final Object value;
    private final Class<?> containerClass;
    private final Integer typeArgumentIndex;
    private int hashCode = -1;
    private String asString;

    private NodeImpl(String name, NodeImpl parent, boolean isIterable, Integer index, Object key, ElementKind kind, Class<?>[] parameterTypes, Integer parameterIndex, Object value, Class<?> containerClass, Integer typeArgumentIndex) {
        this.name = name;
        this.parent = parent;
        this.index = index;
        this.key = key;
        this.value = value;
        this.isIterable = isIterable;
        this.kind = kind;
        this.parameterTypes = parameterTypes;
        this.parameterIndex = parameterIndex;
        this.containerClass = containerClass;
        this.typeArgumentIndex = typeArgumentIndex;
    }

    public static NodeImpl createPropertyNode(String name, NodeImpl parent) {
        return new NodeImpl(name, parent, false, null, null, ElementKind.PROPERTY, EMPTY_CLASS_ARRAY, null, null, null, null);
    }

    public static NodeImpl createContainerElementNode(String name, NodeImpl parent) {
        return new NodeImpl(name, parent, false, null, null, ElementKind.CONTAINER_ELEMENT, EMPTY_CLASS_ARRAY, null, null, null, null);
    }

    public static NodeImpl createParameterNode(String name, NodeImpl parent, int parameterIndex) {
        return new NodeImpl(name, parent, false, null, null, ElementKind.PARAMETER, EMPTY_CLASS_ARRAY, parameterIndex, null, null, null);
    }

    public static NodeImpl createCrossParameterNode(NodeImpl parent) {
        return new NodeImpl(CROSS_PARAMETER_NODE_NAME, parent, false, null, null, ElementKind.CROSS_PARAMETER, EMPTY_CLASS_ARRAY, null, null, null, null);
    }

    public static NodeImpl createMethodNode(String name, NodeImpl parent, Class<?>[] parameterTypes) {
        return new NodeImpl(name, parent, false, null, null, ElementKind.METHOD, parameterTypes, null, null, null, null);
    }

    public static NodeImpl createConstructorNode(String name, NodeImpl parent, Class<?>[] parameterTypes) {
        return new NodeImpl(name, parent, false, null, null, ElementKind.CONSTRUCTOR, parameterTypes, null, null, null, null);
    }

    public static NodeImpl createBeanNode(NodeImpl parent) {
        return new NodeImpl(null, parent, false, null, null, ElementKind.BEAN, EMPTY_CLASS_ARRAY, null, null, null, null);
    }

    public static NodeImpl createReturnValue(NodeImpl parent) {
        return new NodeImpl(RETURN_VALUE_NODE_NAME, parent, false, null, null, ElementKind.RETURN_VALUE, EMPTY_CLASS_ARRAY, null, null, null, null);
    }

    public static NodeImpl makeIterable(NodeImpl node) {
        return new NodeImpl(node.name, node.parent, true, null, null, node.kind, node.parameterTypes, node.parameterIndex, node.value, node.containerClass, node.typeArgumentIndex);
    }

    public static NodeImpl makeIterableAndSetIndex(NodeImpl node, Integer index) {
        return new NodeImpl(node.name, node.parent, true, index, null, node.kind, node.parameterTypes, node.parameterIndex, node.value, node.containerClass, node.typeArgumentIndex);
    }

    public static NodeImpl makeIterableAndSetMapKey(NodeImpl node, Object key) {
        return new NodeImpl(node.name, node.parent, true, null, key, node.kind, node.parameterTypes, node.parameterIndex, node.value, node.containerClass, node.typeArgumentIndex);
    }

    public static NodeImpl setPropertyValue(NodeImpl node, Object value) {
        return new NodeImpl(node.name, node.parent, node.isIterable, node.index, node.key, node.kind, node.parameterTypes, node.parameterIndex, value, node.containerClass, node.typeArgumentIndex);
    }

    public static NodeImpl setTypeParameter(NodeImpl node, Class<?> containerClass, Integer typeArgumentIndex) {
        return new NodeImpl(node.name, node.parent, node.isIterable, node.index, node.key, node.kind, node.parameterTypes, node.parameterIndex, node.value, containerClass, typeArgumentIndex);
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isInIterable() {
        return this.parent != null && this.parent.isIterable();
    }

    public final boolean isIterable() {
        return this.isIterable;
    }

    public final Integer getIndex() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.index;
    }

    public final Object getKey() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.key;
    }

    public Class<?> getContainerClass() {
        Contracts.assertTrue(this.kind == ElementKind.BEAN || this.kind == ElementKind.PROPERTY || this.kind == ElementKind.CONTAINER_ELEMENT, "getContainerClass() may only be invoked for nodes of type ElementKind.BEAN, ElementKind.PROPERTY or ElementKind.CONTAINER_ELEMENT.");
        if (this.parent == null) {
            return null;
        }
        return this.parent.containerClass;
    }

    public Integer getTypeArgumentIndex() {
        Contracts.assertTrue(this.kind == ElementKind.BEAN || this.kind == ElementKind.PROPERTY || this.kind == ElementKind.CONTAINER_ELEMENT, "getTypeArgumentIndex() may only be invoked for nodes of type ElementKind.BEAN, ElementKind.PROPERTY or ElementKind.CONTAINER_ELEMENT.");
        if (this.parent == null) {
            return null;
        }
        return this.parent.typeArgumentIndex;
    }

    public final NodeImpl getParent() {
        return this.parent;
    }

    public ElementKind getKind() {
        return this.kind;
    }

    public <T extends Path.Node> T as(Class<T> nodeType) {
        if (this.kind == ElementKind.BEAN && nodeType == Path.BeanNode.class || this.kind == ElementKind.CONSTRUCTOR && nodeType == Path.ConstructorNode.class || this.kind == ElementKind.CROSS_PARAMETER && nodeType == Path.CrossParameterNode.class || this.kind == ElementKind.METHOD && nodeType == Path.MethodNode.class || this.kind == ElementKind.PARAMETER && nodeType == Path.ParameterNode.class || this.kind == ElementKind.PROPERTY && (nodeType == Path.PropertyNode.class || nodeType == PropertyNode.class) || this.kind == ElementKind.RETURN_VALUE && nodeType == Path.ReturnValueNode.class || this.kind == ElementKind.CONTAINER_ELEMENT && (nodeType == Path.ContainerElementNode.class || nodeType == ContainerElementNode.class)) {
            return (T)((Path.Node)nodeType.cast(this));
        }
        throw LOG.getUnableToNarrowNodeTypeException(this.getClass(), this.kind, nodeType);
    }

    public List<Class<?>> getParameterTypes() {
        return Arrays.asList(this.parameterTypes);
    }

    public int getParameterIndex() {
        Contracts.assertTrue(this.kind == ElementKind.PARAMETER, "getParameterIndex() may only be invoked for nodes of type ElementKind.PARAMETER.");
        return this.parameterIndex;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return this.asString();
    }

    public final String asString() {
        if (this.asString == null) {
            this.asString = this.buildToString();
        }
        return this.asString;
    }

    private String buildToString() {
        StringBuilder builder = new StringBuilder();
        if (this.getName() != null) {
            builder.append(this.getName());
        }
        if (NodeImpl.includeTypeParameterInformation(this.containerClass, this.typeArgumentIndex)) {
            builder.append(TYPE_PARAMETER_OPEN);
            builder.append(TypeVariables.getTypeParameterName(this.containerClass, this.typeArgumentIndex));
            builder.append(TYPE_PARAMETER_CLOSE);
        }
        if (this.isIterable()) {
            builder.append(INDEX_OPEN);
            if (this.index != null) {
                builder.append(this.index);
            } else if (this.key != null) {
                builder.append(this.key);
            }
            builder.append(INDEX_CLOSE);
        }
        return builder.toString();
    }

    private static boolean includeTypeParameterInformation(Class<?> containerClass, Integer typeArgumentIndex) {
        if (containerClass == null || typeArgumentIndex == null) {
            return false;
        }
        if (containerClass.getTypeParameters().length < 2) {
            return false;
        }
        return !Map.class.isAssignableFrom(containerClass) || typeArgumentIndex != 1;
    }

    public final int buildHashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.index == null ? 0 : this.index.hashCode());
        result = 31 * result + (this.isIterable ? 1231 : 1237);
        result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
        result = 31 * result + (this.kind == null ? 0 : this.kind.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.parameterIndex == null ? 0 : this.parameterIndex.hashCode());
        result = 31 * result + (this.parameterTypes == null ? 0 : Arrays.hashCode(this.parameterTypes));
        result = 31 * result + (this.containerClass == null ? 0 : this.containerClass.hashCode());
        result = 31 * result + (this.typeArgumentIndex == null ? 0 : this.typeArgumentIndex.hashCode());
        return result;
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.buildHashCode();
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        NodeImpl other = (NodeImpl)obj;
        if (this.index == null ? other.index != null : !this.index.equals(other.index)) {
            return false;
        }
        if (this.isIterable != other.isIterable) {
            return false;
        }
        if (this.key == null ? other.key != null : !this.key.equals(other.key)) {
            return false;
        }
        if (this.containerClass == null ? other.containerClass != null : !this.containerClass.equals(other.containerClass)) {
            return false;
        }
        if (this.typeArgumentIndex == null ? other.typeArgumentIndex != null : !this.typeArgumentIndex.equals(other.typeArgumentIndex)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        if (this.parameterIndex == null ? other.parameterIndex != null : !this.parameterIndex.equals(other.parameterIndex)) {
            return false;
        }
        return !(this.parameterTypes == null ? other.parameterTypes != null : !Arrays.equals(this.parameterTypes, other.parameterTypes));
    }
}

