/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.Path
 *  javax.validation.Path$Node
 */
package org.hibernate.validator.internal.engine.path;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ElementKind;
import javax.validation.Path;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;

public final class PathImpl
implements Path,
Serializable {
    private static final long serialVersionUID = 7564511574909882392L;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String PROPERTY_PATH_SEPARATOR = ".";
    private static final String LEADING_PROPERTY_GROUP = "[^\\[\\.]+";
    private static final String OPTIONAL_INDEX_GROUP = "\\[(\\w*)\\]";
    private static final String REMAINING_PROPERTY_STRING = "\\.(.*)";
    private static final Pattern PATH_PATTERN = Pattern.compile("([^\\[\\.]+)(\\[(\\w*)\\])?(\\.(.*))*");
    private static final int PROPERTY_NAME_GROUP = 1;
    private static final int INDEXED_GROUP = 2;
    private static final int INDEX_GROUP = 3;
    private static final int REMAINING_STRING_GROUP = 5;
    private List<Path.Node> nodeList;
    private boolean nodeListRequiresCopy;
    private NodeImpl currentLeafNode;
    private int hashCode;

    public static PathImpl createPathFromString(String propertyPath) {
        Contracts.assertNotNull(propertyPath, Messages.MESSAGES.propertyPathCannotBeNull());
        if (propertyPath.length() == 0) {
            return PathImpl.createRootPath();
        }
        return PathImpl.parseProperty(propertyPath);
    }

    public static PathImpl createPathForExecutable(ExecutableMetaData executable) {
        Contracts.assertNotNull(executable, "A method is required to create a method return value path.");
        PathImpl path = PathImpl.createRootPath();
        if (executable.getKind() == ElementKind.CONSTRUCTOR) {
            path.addConstructorNode(executable.getName(), executable.getParameterTypes());
        } else {
            path.addMethodNode(executable.getName(), executable.getParameterTypes());
        }
        return path;
    }

    public static PathImpl createRootPath() {
        PathImpl path = new PathImpl();
        path.addBeanNode();
        return path;
    }

    public static PathImpl createCopy(PathImpl path) {
        return new PathImpl(path);
    }

    public boolean isRootPath() {
        return this.nodeList.size() == 1 && this.nodeList.get(0).getName() == null;
    }

    public PathImpl getPathWithoutLeafNode() {
        return new PathImpl(this.nodeList.subList(0, this.nodeList.size() - 1));
    }

    public NodeImpl addPropertyNode(String nodeName) {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createPropertyNode(nodeName, parent);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addContainerElementNode(String nodeName) {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createContainerElementNode(nodeName, parent);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addParameterNode(String nodeName, int index) {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createParameterNode(nodeName, parent, index);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addCrossParameterNode() {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createCrossParameterNode(parent);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addBeanNode() {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createBeanNode(parent);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addReturnValueNode() {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createReturnValue(parent);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    private NodeImpl addConstructorNode(String name, Class<?>[] parameterTypes) {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createConstructorNode(name, parent, parameterTypes);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    private NodeImpl addMethodNode(String name, Class<?>[] parameterTypes) {
        this.requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createMethodNode(name, parent, parameterTypes);
        this.nodeList.add((Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterable() {
        this.requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterable(this.currentLeafNode);
        this.nodeList.set(this.nodeList.size() - 1, (Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterableAndSetIndex(Integer index) {
        this.requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterableAndSetIndex(this.currentLeafNode, index);
        this.nodeList.set(this.nodeList.size() - 1, (Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterableAndSetMapKey(Object key) {
        this.requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterableAndSetMapKey(this.currentLeafNode, key);
        this.nodeList.set(this.nodeList.size() - 1, (Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl setLeafNodeValueIfRequired(Object value) {
        if (this.currentLeafNode.getKind() == ElementKind.PROPERTY || this.currentLeafNode.getKind() == ElementKind.CONTAINER_ELEMENT) {
            this.requiresWriteableNodeList();
            this.currentLeafNode = NodeImpl.setPropertyValue(this.currentLeafNode, value);
            this.nodeList.set(this.nodeList.size() - 1, (Path.Node)this.currentLeafNode);
        }
        return this.currentLeafNode;
    }

    public NodeImpl setLeafNodeTypeParameter(Class<?> containerClass, Integer typeArgumentIndex) {
        this.requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.setTypeParameter(this.currentLeafNode, containerClass, typeArgumentIndex);
        this.nodeList.set(this.nodeList.size() - 1, (Path.Node)this.currentLeafNode);
        this.resetHashCode();
        return this.currentLeafNode;
    }

    public void removeLeafNode() {
        if (!this.nodeList.isEmpty()) {
            this.requiresWriteableNodeList();
            this.nodeList.remove(this.nodeList.size() - 1);
            this.currentLeafNode = this.nodeList.isEmpty() ? null : (NodeImpl)this.nodeList.get(this.nodeList.size() - 1);
            this.resetHashCode();
        }
    }

    public NodeImpl getLeafNode() {
        return this.currentLeafNode;
    }

    public Iterator<Path.Node> iterator() {
        if (this.nodeList.size() == 0) {
            return Collections.emptyList().iterator();
        }
        if (this.nodeList.size() == 1) {
            return this.nodeList.iterator();
        }
        return this.nodeList.subList(1, this.nodeList.size()).iterator();
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 1; i < this.nodeList.size(); ++i) {
            NodeImpl nodeImpl = (NodeImpl)this.nodeList.get(i);
            String name = nodeImpl.asString();
            if (name.isEmpty()) continue;
            if (!first) {
                builder.append(PROPERTY_PATH_SEPARATOR);
            }
            builder.append(nodeImpl.asString());
            first = false;
        }
        return builder.toString();
    }

    private void requiresWriteableNodeList() {
        if (!this.nodeListRequiresCopy) {
            return;
        }
        ArrayList<Path.Node> newNodeList = new ArrayList<Path.Node>(this.nodeList.size() + 1);
        newNodeList.addAll(this.nodeList);
        this.nodeList = newNodeList;
        this.nodeListRequiresCopy = false;
    }

    public String toString() {
        return this.asString();
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
        PathImpl other = (PathImpl)obj;
        return !(this.nodeList == null ? other.nodeList != null : !this.nodeList.equals(other.nodeList));
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.buildHashCode();
        }
        return this.hashCode;
    }

    private int buildHashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.nodeList == null ? 0 : this.nodeList.hashCode());
        return result;
    }

    private PathImpl(PathImpl path) {
        this.nodeList = path.nodeList;
        this.currentLeafNode = path.currentLeafNode;
        this.hashCode = path.hashCode;
        this.nodeListRequiresCopy = true;
    }

    private PathImpl() {
        this.nodeList = new ArrayList<Path.Node>(1);
        this.hashCode = -1;
        this.nodeListRequiresCopy = false;
    }

    private PathImpl(List<Path.Node> nodeList) {
        this.nodeList = nodeList;
        this.currentLeafNode = (NodeImpl)nodeList.get(nodeList.size() - 1);
        this.hashCode = -1;
        this.nodeListRequiresCopy = true;
    }

    private void resetHashCode() {
        this.hashCode = -1;
    }

    private static PathImpl parseProperty(String propertyName) {
        PathImpl path = PathImpl.createRootPath();
        String tmp = propertyName;
        do {
            Matcher matcher;
            if ((matcher = PATH_PATTERN.matcher(tmp)).matches()) {
                String indexOrKey;
                String value = matcher.group(1);
                if (!PathImpl.isValidJavaIdentifier(value)) {
                    throw LOG.getInvalidJavaIdentifierException(value);
                }
                path.addPropertyNode(value);
                if (matcher.group(2) != null) {
                    path.makeLeafNodeIterable();
                }
                if ((indexOrKey = matcher.group(3)) != null && indexOrKey.length() > 0) {
                    try {
                        Integer i = Integer.parseInt(indexOrKey);
                        path.makeLeafNodeIterableAndSetIndex(i);
                    }
                    catch (NumberFormatException e) {
                        path.makeLeafNodeIterableAndSetMapKey(indexOrKey);
                    }
                }
            } else {
                throw LOG.getUnableToParsePropertyPathException(propertyName);
            }
            tmp = matcher.group(5);
        } while (tmp != null);
        if (path.getLeafNode().isIterable()) {
            path.addBeanNode();
        }
        return path;
    }

    private static boolean isValidJavaIdentifier(String identifier) {
        Contracts.assertNotNull(identifier, "identifier param cannot be null");
        if (identifier.length() == 0 || !Character.isJavaIdentifierStart((int)identifier.charAt(0))) {
            return false;
        }
        for (int i = 1; i < identifier.length(); ++i) {
            if (Character.isJavaIdentifierPart((int)identifier.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

