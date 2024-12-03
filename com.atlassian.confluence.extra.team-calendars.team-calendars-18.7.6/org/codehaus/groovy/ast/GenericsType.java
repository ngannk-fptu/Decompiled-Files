/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.ast.tools.WideningCategories;

public class GenericsType
extends ASTNode {
    public static final GenericsType[] EMPTY_ARRAY = new GenericsType[0];
    private final ClassNode[] upperBounds;
    private final ClassNode lowerBound;
    private ClassNode type;
    private String name;
    private boolean placeholder;
    private boolean resolved;
    private boolean wildcard;

    public GenericsType(ClassNode type, ClassNode[] upperBounds, ClassNode lowerBound) {
        this.type = type;
        this.name = type.isGenericsPlaceHolder() ? type.getUnresolvedName() : type.getName();
        this.upperBounds = upperBounds;
        this.lowerBound = lowerBound;
        this.placeholder = type.isGenericsPlaceHolder();
        this.resolved = false;
    }

    public GenericsType(ClassNode basicType) {
        this(basicType, null, null);
    }

    public ClassNode getType() {
        return this.type;
    }

    public void setType(ClassNode type) {
        this.type = type;
    }

    public String toString() {
        HashSet<String> visited = new HashSet<String>();
        return this.toString(visited);
    }

    private String toString(Set<String> visited) {
        if (this.placeholder) {
            visited.add(this.name);
        }
        StringBuilder ret = new StringBuilder(this.wildcard ? "?" : (this.type == null || this.placeholder ? this.name : this.genericsBounds(this.type, visited)));
        if (this.upperBounds != null) {
            if (!this.placeholder || this.upperBounds.length != 1 || this.upperBounds[0].isGenericsPlaceHolder() || !this.upperBounds[0].getName().equals("java.lang.Object")) {
                ret.append(" extends ");
                for (int i = 0; i < this.upperBounds.length; ++i) {
                    ret.append(this.genericsBounds(this.upperBounds[i], visited));
                    if (i + 1 >= this.upperBounds.length) continue;
                    ret.append(" & ");
                }
            }
        } else if (this.lowerBound != null) {
            ret.append(" super ").append(this.genericsBounds(this.lowerBound, visited));
        }
        return ret.toString();
    }

    private String nameOf(ClassNode theType) {
        StringBuilder ret = new StringBuilder();
        if (theType.isArray()) {
            ret.append(this.nameOf(theType.getComponentType()));
            ret.append("[]");
        } else {
            ret.append(theType.getName());
        }
        return ret.toString();
    }

    private String genericsBounds(ClassNode theType, Set<String> visited) {
        StringBuilder ret = new StringBuilder();
        if (theType.isArray()) {
            ret.append(this.nameOf(theType));
        } else if (theType.redirect() instanceof InnerClassNode) {
            InnerClassNode innerClassNode = (InnerClassNode)theType.redirect();
            String parentClassNodeName = innerClassNode.getOuterClass().getName();
            if (Modifier.isStatic(innerClassNode.getModifiers()) || innerClassNode.isInterface()) {
                ret.append(innerClassNode.getOuterClass().getName());
            } else {
                ret.append(this.genericsBounds(innerClassNode.getOuterClass(), new HashSet<String>()));
            }
            ret.append(".");
            String typeName = theType.getName();
            ret.append(typeName.substring(parentClassNodeName.length() + 1));
        } else {
            ret.append(theType.getName());
        }
        GenericsType[] genericsTypes = theType.getGenericsTypes();
        if (genericsTypes == null || genericsTypes.length == 0) {
            return ret.toString();
        }
        if (genericsTypes.length == 1 && genericsTypes[0].isPlaceholder() && theType.getName().equals("java.lang.Object")) {
            return genericsTypes[0].getName();
        }
        ret.append("<");
        for (int i = 0; i < genericsTypes.length; ++i) {
            GenericsType type;
            if (i != 0) {
                ret.append(", ");
            }
            if ((type = genericsTypes[i]).isPlaceholder() && visited.contains(type.getName())) {
                ret.append(type.getName());
                continue;
            }
            ret.append(type.toString(visited));
        }
        ret.append(">");
        return ret.toString();
    }

    public ClassNode[] getUpperBounds() {
        return this.upperBounds;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
        this.type.setGenericsPlaceHolder(placeholder);
    }

    public boolean isResolved() {
        return this.resolved || this.placeholder;
    }

    public void setResolved(boolean res) {
        this.resolved = res;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWildcard() {
        return this.wildcard;
    }

    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }

    public ClassNode getLowerBound() {
        return this.lowerBound;
    }

    public boolean isCompatibleWith(ClassNode classNode) {
        return new GenericsTypeMatcher().matches(classNode);
    }

    private static ClassNode getParameterizedSuperClass(ClassNode classNode) {
        if (ClassHelper.OBJECT_TYPE.equals(classNode)) {
            return null;
        }
        ClassNode superClass = classNode.getUnresolvedSuperClass();
        if (superClass == null) {
            return ClassHelper.OBJECT_TYPE;
        }
        if (!classNode.isUsingGenerics() || !superClass.isUsingGenerics()) {
            return superClass;
        }
        GenericsType[] genericsTypes = classNode.getGenericsTypes();
        GenericsType[] redirectGenericTypes = classNode.redirect().getGenericsTypes();
        superClass = superClass.getPlainNodeReference();
        if (genericsTypes == null || redirectGenericTypes == null || superClass.getGenericsTypes() == null) {
            return superClass;
        }
        int genericsTypesLength = genericsTypes.length;
        for (int i = 0; i < genericsTypesLength; ++i) {
            if (!redirectGenericTypes[i].isPlaceholder()) continue;
            GenericsType genericsType = genericsTypes[i];
            for (GenericsType superGenericType : superClass.getGenericsTypes()) {
                if (!superGenericType.isPlaceholder() || !superGenericType.getName().equals(redirectGenericTypes[i].getName())) continue;
                superGenericTypes[j] = genericsType;
            }
        }
        return superClass;
    }

    private class GenericsTypeMatcher {
        private GenericsTypeMatcher() {
        }

        public boolean implementsInterfaceOrIsSubclassOf(ClassNode type, ClassNode superOrInterface) {
            boolean result;
            boolean bl = result = type.equals(superOrInterface) || type.isDerivedFrom(superOrInterface) || type.implementsInterface(superOrInterface);
            if (result) {
                return true;
            }
            if (ClassHelper.GROOVY_OBJECT_TYPE.equals(superOrInterface) && type.getCompileUnit() != null) {
                return true;
            }
            if (superOrInterface instanceof WideningCategories.LowestUpperBoundClassNode) {
                WideningCategories.LowestUpperBoundClassNode cn = (WideningCategories.LowestUpperBoundClassNode)superOrInterface;
                result = this.implementsInterfaceOrIsSubclassOf(type, cn.getSuperClass());
                if (result) {
                    ClassNode interfaceNode;
                    ClassNode[] classNodeArray = cn.getInterfaces();
                    int n = classNodeArray.length;
                    for (int i = 0; i < n && (result = this.implementsInterfaceOrIsSubclassOf(type, interfaceNode = classNodeArray[i])); ++i) {
                    }
                }
                if (result) {
                    return true;
                }
            }
            if (type.isArray() && superOrInterface.isArray()) {
                return this.implementsInterfaceOrIsSubclassOf(type.getComponentType(), superOrInterface.getComponentType());
            }
            return false;
        }

        public boolean matches(ClassNode classNode) {
            GenericsType[] genericsTypes = classNode.getGenericsTypes();
            if (genericsTypes != null && genericsTypes.length == 0) {
                return true;
            }
            if (classNode.isGenericsPlaceHolder()) {
                if (genericsTypes == null) {
                    return true;
                }
                if (GenericsType.this.isWildcard()) {
                    if (GenericsType.this.lowerBound != null) {
                        return genericsTypes[0].getName().equals(GenericsType.this.lowerBound.getUnresolvedName());
                    }
                    if (GenericsType.this.upperBounds != null) {
                        for (ClassNode upperBound : GenericsType.this.upperBounds) {
                            String name = upperBound.getGenericsTypes()[0].getName();
                            if (!genericsTypes[0].getName().equals(name)) continue;
                            return true;
                        }
                        return false;
                    }
                }
                return genericsTypes[0].getName().equals(GenericsType.this.name);
            }
            if (GenericsType.this.wildcard || GenericsType.this.placeholder) {
                if (GenericsType.this.upperBounds != null) {
                    boolean upIsOk = true;
                    int upperBoundsLength = GenericsType.this.upperBounds.length;
                    for (int i = 0; i < upperBoundsLength && upIsOk; ++i) {
                        ClassNode upperBound = GenericsType.this.upperBounds[i];
                        upIsOk = this.implementsInterfaceOrIsSubclassOf(classNode, upperBound);
                    }
                    upIsOk = upIsOk && this.checkGenerics(classNode);
                    return upIsOk;
                }
                if (GenericsType.this.lowerBound != null) {
                    return this.implementsInterfaceOrIsSubclassOf(GenericsType.this.lowerBound, classNode) && this.checkGenerics(classNode);
                }
                return true;
            }
            if (GenericsType.this.type != null && !GenericsType.this.type.equals(classNode)) {
                return false;
            }
            return GenericsType.this.type == null || this.compareGenericsWithBound(classNode, GenericsType.this.type);
        }

        private boolean checkGenerics(ClassNode classNode) {
            if (GenericsType.this.upperBounds != null) {
                for (ClassNode upperBound : GenericsType.this.upperBounds) {
                    if (this.compareGenericsWithBound(classNode, upperBound)) continue;
                    return false;
                }
            }
            return GenericsType.this.lowerBound == null || GenericsType.this.lowerBound.redirect().isUsingGenerics() || this.compareGenericsWithBound(classNode, GenericsType.this.lowerBound);
        }

        private boolean compareGenericsWithBound(ClassNode classNode, ClassNode bound) {
            if (classNode == null) {
                return false;
            }
            if (!bound.isUsingGenerics() || classNode.getGenericsTypes() == null && classNode.redirect().getGenericsTypes() != null) {
                return true;
            }
            if (!classNode.equals(bound)) {
                boolean success;
                if (bound.isInterface()) {
                    Set<ClassNode> interfaces = classNode.getAllInterfaces();
                    for (ClassNode classNode2 : interfaces) {
                        if (!classNode2.equals(bound)) continue;
                        ClassNode node = GenericsUtils.parameterizeType(classNode, classNode2);
                        return this.compareGenericsWithBound(node, bound);
                    }
                }
                if (bound instanceof WideningCategories.LowestUpperBoundClassNode && (success = this.compareGenericsWithBound(classNode, bound.getSuperClass()))) {
                    ClassNode[] interfaces;
                    for (ClassNode anInterface : interfaces = bound.getInterfaces()) {
                        if (!(success &= this.compareGenericsWithBound(classNode, anInterface))) break;
                    }
                    if (success) {
                        return true;
                    }
                }
                return this.compareGenericsWithBound(GenericsType.getParameterizedSuperClass(classNode), bound);
            }
            GenericsType[] cnTypes = classNode.getGenericsTypes();
            if (cnTypes == null && classNode.isRedirectNode()) {
                cnTypes = classNode.redirect().getGenericsTypes();
            }
            if (cnTypes == null) {
                return true;
            }
            GenericsType[] redirectBoundGenericTypes = bound.redirect().getGenericsTypes();
            Map<String, GenericsType> map = GenericsUtils.extractPlaceholders(classNode);
            Map<String, GenericsType> boundPlaceHolders = GenericsUtils.extractPlaceholders(bound);
            boolean match = true;
            for (int i = 0; redirectBoundGenericTypes != null && i < redirectBoundGenericTypes.length && match; ++i) {
                String name;
                GenericsType redirectBoundType = redirectBoundGenericTypes[i];
                GenericsType classNodeType = cnTypes[i];
                if (classNodeType.isPlaceholder()) {
                    name = classNodeType.getName();
                    if (redirectBoundType.isPlaceholder()) {
                        match = name.equals(redirectBoundType.getName());
                        if (match) continue;
                        GenericsType genericsType = boundPlaceHolders.get(redirectBoundType.getName());
                        match = false;
                        if (genericsType == null) continue;
                        if (genericsType.isPlaceholder()) {
                            match = true;
                            continue;
                        }
                        if (!genericsType.isWildcard() || genericsType.getUpperBounds() == null) continue;
                        for (ClassNode up : genericsType.getUpperBounds()) {
                            match |= redirectBoundType.isCompatibleWith(up);
                        }
                        if (genericsType.getLowerBound() == null) continue;
                        match |= redirectBoundType.isCompatibleWith(genericsType.getLowerBound());
                        continue;
                    }
                    if (map.containsKey(name)) {
                        classNodeType = map.get(name);
                    }
                    match = classNodeType.isCompatibleWith(redirectBoundType.getType());
                    continue;
                }
                if (redirectBoundType.isPlaceholder()) {
                    if (classNodeType.isPlaceholder()) {
                        match = classNodeType.getName().equals(redirectBoundType.getName());
                        continue;
                    }
                    name = redirectBoundType.getName();
                    if (boundPlaceHolders.containsKey(name)) {
                        redirectBoundType = boundPlaceHolders.get(name);
                        boolean wildcard = redirectBoundType.isWildcard();
                        boolean placeholder = redirectBoundType.isPlaceholder();
                        if (placeholder || wildcard) {
                            if (wildcard) {
                                if (redirectBoundType.lowerBound != null) {
                                    GenericsType gt = new GenericsType(redirectBoundType.lowerBound);
                                    if (gt.isPlaceholder() && map.containsKey(gt.getName())) {
                                        gt = map.get(gt.getName());
                                    }
                                    match = this.implementsInterfaceOrIsSubclassOf(gt.getType(), classNodeType.getType());
                                }
                                if (match && redirectBoundType.upperBounds != null) {
                                    for (ClassNode upperBound : redirectBoundType.upperBounds) {
                                        GenericsType gt = new GenericsType(upperBound);
                                        if (gt.isPlaceholder() && map.containsKey(gt.getName())) {
                                            gt = map.get(gt.getName());
                                        }
                                        boolean bl = match = this.implementsInterfaceOrIsSubclassOf(classNodeType.getType(), gt.getType()) || classNodeType.isCompatibleWith(gt.getType());
                                        if (!match) break;
                                    }
                                }
                                return match;
                            }
                            if (map.containsKey(name)) {
                                redirectBoundType = map.get(name);
                            }
                        }
                    }
                    match = redirectBoundType.isCompatibleWith(classNodeType.getType());
                    continue;
                }
                match = redirectBoundType.isWildcard() || classNodeType.isCompatibleWith(redirectBoundType.getType());
            }
            return match;
        }
    }
}

