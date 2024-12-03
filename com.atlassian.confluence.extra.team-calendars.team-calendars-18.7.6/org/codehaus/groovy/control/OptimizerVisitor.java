/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class OptimizerVisitor
extends ClassCodeExpressionTransformer {
    private ClassNode currentClass;
    private SourceUnit source;
    private final Map<Object, FieldNode> const2Objects = new HashMap<Object, FieldNode>();
    private final Map<Object, FieldNode> const2Prims = new HashMap<Object, FieldNode>();
    private int index;
    private final List<FieldNode> missingFields = new LinkedList<FieldNode>();

    public OptimizerVisitor(CompilationUnit cu) {
    }

    public void visitClass(ClassNode node, SourceUnit source) {
        this.currentClass = node;
        this.source = source;
        this.const2Objects.clear();
        this.const2Prims.clear();
        this.missingFields.clear();
        this.index = 0;
        super.visitClass(node);
        this.addMissingFields();
        this.pruneUnneededGroovyObjectInterface(node);
    }

    private void pruneUnneededGroovyObjectInterface(ClassNode node) {
        ClassNode superClass = node.getSuperClass();
        boolean isSuperGroovy = superClass.isDerivedFromGroovyObject();
        if (isSuperGroovy) {
            ClassNode[] interfaces = node.getInterfaces();
            boolean needsFix = false;
            for (ClassNode classNode : interfaces) {
                if (!classNode.equals(ClassHelper.GROOVY_OBJECT_TYPE)) continue;
                needsFix = true;
                break;
            }
            if (needsFix) {
                ArrayList<ClassNode> newInterfaces = new ArrayList<ClassNode>(interfaces.length);
                for (ClassNode classNode : interfaces) {
                    if (classNode.equals(ClassHelper.GROOVY_OBJECT_TYPE)) continue;
                    newInterfaces.add(classNode);
                }
                node.setInterfaces(newInterfaces.toArray(new ClassNode[newInterfaces.size()]));
            }
        }
    }

    private void addMissingFields() {
        for (FieldNode f : this.missingFields) {
            this.currentClass.addField(f);
        }
    }

    private void setConstField(ConstantExpression constantExpression) {
        String name;
        FieldNode field;
        Object n = constantExpression.getValue();
        if (!(n instanceof Number)) {
            return;
        }
        if (n instanceof Integer || n instanceof Double) {
            return;
        }
        if (n instanceof Long && (0L == (Long)n || 1L == (Long)n)) {
            return;
        }
        boolean isPrimitive = ClassHelper.isPrimitiveType(constantExpression.getType());
        FieldNode fieldNode = field = isPrimitive ? this.const2Prims.get(n) : this.const2Objects.get(n);
        if (field != null) {
            constantExpression.setConstantName(field.getName());
            return;
        }
        while (this.currentClass.getDeclaredField(name = "$const$" + this.index++) != null) {
        }
        field = new FieldNode(name, 4122, constantExpression.getType(), this.currentClass, constantExpression);
        field.setSynthetic(true);
        this.missingFields.add(field);
        constantExpression.setConstantName(field.getName());
        if (isPrimitive) {
            this.const2Prims.put(n, field);
        } else {
            this.const2Objects.put(n, field);
        }
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp == null) {
            return null;
        }
        if (!this.currentClass.isInterface() && exp.getClass() == ConstantExpression.class) {
            this.setConstField((ConstantExpression)exp);
        }
        return exp.transformExpression(this);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
    }
}

