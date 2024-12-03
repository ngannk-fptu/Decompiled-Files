/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.MixinNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.transform.ASTTransformation;

class UnionTypeClassNode
extends ClassNode {
    private final ClassNode[] delegates;

    public UnionTypeClassNode(ClassNode ... classNodes) {
        super("<UnionType:" + UnionTypeClassNode.asArrayDescriptor(classNodes) + ">", 0, ClassHelper.OBJECT_TYPE);
        this.delegates = classNodes == null ? ClassNode.EMPTY_ARRAY : classNodes;
    }

    private static String asArrayDescriptor(ClassNode ... nodes) {
        StringBuilder sb = new StringBuilder();
        for (ClassNode node : nodes) {
            if (sb.length() > 0) {
                sb.append("+");
            }
            sb.append(node.getText());
        }
        return sb.toString();
    }

    public ClassNode[] getDelegates() {
        return this.delegates;
    }

    @Override
    public ConstructorNode addConstructor(int modifiers, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addConstructor(ConstructorNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNode addField(String name, int modifiers, ClassNode type, Expression initialValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addField(FieldNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNode addFieldFirst(String name, int modifiers, ClassNode type, Expression initialValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFieldFirst(FieldNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addInterface(ClassNode type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodNode addMethod(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMethod(MethodNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMixin(MixinNode mixin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addObjectInitializerStatements(Statement statements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PropertyNode addProperty(String name, int modifiers, ClassNode type, Expression initialValueExpression, Statement getterBlock, Statement setterBlock) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProperty(PropertyNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addStaticInitializerStatements(List<Statement> staticStatements, boolean fieldInit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodNode addSyntheticMethod(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTransform(Class<? extends ASTTransformation> transform, ASTNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean declaresInterface(ClassNode classNode) {
        for (ClassNode delegate : this.delegates) {
            if (!delegate.declaresInterface(classNode)) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<MethodNode> getAbstractMethods() {
        LinkedList<MethodNode> allMethods = new LinkedList<MethodNode>();
        for (ClassNode delegate : this.delegates) {
            allMethods.addAll(delegate.getAbstractMethods());
        }
        return allMethods;
    }

    @Override
    public List<MethodNode> getAllDeclaredMethods() {
        LinkedList<MethodNode> allMethods = new LinkedList<MethodNode>();
        for (ClassNode delegate : this.delegates) {
            allMethods.addAll(delegate.getAllDeclaredMethods());
        }
        return allMethods;
    }

    @Override
    public Set<ClassNode> getAllInterfaces() {
        HashSet<ClassNode> allMethods = new HashSet<ClassNode>();
        for (ClassNode delegate : this.delegates) {
            allMethods.addAll(delegate.getAllInterfaces());
        }
        return allMethods;
    }

    @Override
    public List<AnnotationNode> getAnnotations() {
        LinkedList<AnnotationNode> nodes = new LinkedList<AnnotationNode>();
        for (ClassNode delegate : this.delegates) {
            List<AnnotationNode> annotations = delegate.getAnnotations();
            if (annotations == null) continue;
            nodes.addAll(annotations);
        }
        return nodes;
    }

    @Override
    public List<AnnotationNode> getAnnotations(ClassNode type) {
        LinkedList<AnnotationNode> nodes = new LinkedList<AnnotationNode>();
        for (ClassNode delegate : this.delegates) {
            List<AnnotationNode> annotations = delegate.getAnnotations(type);
            if (annotations == null) continue;
            nodes.addAll(annotations);
        }
        return nodes;
    }

    @Override
    public ClassNode getComponentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ConstructorNode> getDeclaredConstructors() {
        LinkedList<ConstructorNode> nodes = new LinkedList<ConstructorNode>();
        for (ClassNode delegate : this.delegates) {
            nodes.addAll(delegate.getDeclaredConstructors());
        }
        return nodes;
    }

    @Override
    public FieldNode getDeclaredField(String name) {
        for (ClassNode delegate : this.delegates) {
            FieldNode node = delegate.getDeclaredField(name);
            if (node == null) continue;
            return node;
        }
        return null;
    }

    @Override
    public MethodNode getDeclaredMethod(String name, Parameter[] parameters) {
        for (ClassNode delegate : this.delegates) {
            MethodNode node = delegate.getDeclaredMethod(name, parameters);
            if (node == null) continue;
            return node;
        }
        return null;
    }

    @Override
    public List<MethodNode> getDeclaredMethods(String name) {
        LinkedList<MethodNode> nodes = new LinkedList<MethodNode>();
        for (ClassNode delegate : this.delegates) {
            List<MethodNode> methods = delegate.getDeclaredMethods(name);
            if (methods == null) continue;
            nodes.addAll(methods);
        }
        return nodes;
    }

    @Override
    public Map<String, MethodNode> getDeclaredMethodsMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodNode getEnclosingMethod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNode getField(String name) {
        for (ClassNode delegate : this.delegates) {
            FieldNode field = delegate.getField(name);
            if (field == null) continue;
            return field;
        }
        return null;
    }

    @Override
    public List<FieldNode> getFields() {
        LinkedList<FieldNode> nodes = new LinkedList<FieldNode>();
        for (ClassNode delegate : this.delegates) {
            List<FieldNode> fields = delegate.getFields();
            if (fields == null) continue;
            nodes.addAll(fields);
        }
        return nodes;
    }

    @Override
    public Iterator<InnerClassNode> getInnerClasses() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassNode[] getInterfaces() {
        LinkedHashSet nodes = new LinkedHashSet();
        for (ClassNode delegate : this.delegates) {
            ClassNode[] interfaces = delegate.getInterfaces();
            if (interfaces == null) continue;
            Collections.addAll(nodes, interfaces);
        }
        return nodes.toArray(new ClassNode[nodes.size()]);
    }

    @Override
    public List<MethodNode> getMethods() {
        LinkedList<MethodNode> nodes = new LinkedList<MethodNode>();
        for (ClassNode delegate : this.delegates) {
            List<MethodNode> methods = delegate.getMethods();
            if (methods == null) continue;
            nodes.addAll(methods);
        }
        return nodes;
    }

    @Override
    public List<PropertyNode> getProperties() {
        LinkedList<PropertyNode> nodes = new LinkedList<PropertyNode>();
        for (ClassNode delegate : this.delegates) {
            List<PropertyNode> properties = delegate.getProperties();
            if (properties == null) continue;
            nodes.addAll(properties);
        }
        return nodes;
    }

    @Override
    public Class getTypeClass() {
        return super.getTypeClass();
    }

    @Override
    public ClassNode[] getUnresolvedInterfaces() {
        LinkedHashSet nodes = new LinkedHashSet();
        for (ClassNode delegate : this.delegates) {
            ClassNode[] interfaces = delegate.getUnresolvedInterfaces();
            if (interfaces == null) continue;
            Collections.addAll(nodes, interfaces);
        }
        return nodes.toArray(new ClassNode[nodes.size()]);
    }

    @Override
    public ClassNode[] getUnresolvedInterfaces(boolean useRedirect) {
        LinkedHashSet nodes = new LinkedHashSet();
        for (ClassNode delegate : this.delegates) {
            ClassNode[] interfaces = delegate.getUnresolvedInterfaces(useRedirect);
            if (interfaces == null) continue;
            Collections.addAll(nodes, interfaces);
        }
        return nodes.toArray(new ClassNode[nodes.size()]);
    }

    @Override
    public int hashCode() {
        int hash = 13;
        for (ClassNode delegate : this.delegates) {
            hash = 31 * hash + delegate.hashCode();
        }
        return hash;
    }

    @Override
    public boolean implementsInterface(ClassNode classNode) {
        for (ClassNode delegate : this.delegates) {
            if (!delegate.implementsInterface(classNode)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAnnotated() {
        for (ClassNode delegate : this.delegates) {
            if (!delegate.isAnnotated()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDerivedFrom(ClassNode type) {
        for (ClassNode delegate : this.delegates) {
            if (!delegate.isDerivedFrom(type)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDerivedFromGroovyObject() {
        for (ClassNode delegate : this.delegates) {
            if (!delegate.isDerivedFromGroovyObject()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void removeField(String oldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renameField(String oldName, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAnnotated(boolean flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEnclosingMethod(MethodNode enclosingMethod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGenericsPlaceHolder(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGenericsTypes(GenericsType[] genericsTypes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInterfaces(ClassNode[] interfaces) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setModifiers(int modifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRedirect(ClassNode cn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScript(boolean script) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScriptBody(boolean scriptBody) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStaticClass(boolean staticClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSuperClass(ClassNode superClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSyntheticPublic(boolean syntheticPublic) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnresolvedSuperClass(ClassNode sn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUsingGenerics(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitContents(GroovyClassVisitor visitor) {
        for (ClassNode delegate : this.delegates) {
            delegate.visitContents(visitor);
        }
    }
}

