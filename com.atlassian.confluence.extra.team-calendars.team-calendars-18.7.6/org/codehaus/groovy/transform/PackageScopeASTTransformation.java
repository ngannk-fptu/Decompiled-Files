/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.PackageScope;
import groovy.transform.PackageScopeTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PackageScopeASTTransformation
extends AbstractASTTransformation {
    private static final Class MY_CLASS = PackageScope.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final String LEGACY_TYPE_NAME = "groovy.lang.PackageScope";
    private static final Class TARGET_CLASS = PackageScopeTarget.class;
    private static final String TARGET_CLASS_NAME = ClassHelper.make(TARGET_CLASS).getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        boolean legacyMode = LEGACY_TYPE_NAME.equals(node.getClassNode().getName());
        if (!MY_TYPE.equals(node.getClassNode()) && !legacyMode) {
            return;
        }
        Expression value = node.getMember("value");
        if (parent instanceof ClassNode) {
            List<PackageScopeTarget> targets = value == null ? Arrays.asList(legacyMode ? PackageScopeTarget.FIELDS : PackageScopeTarget.CLASS) : this.determineTargets(value);
            this.visitClassNode((ClassNode)parent, targets);
            parent.getAnnotations();
        } else {
            if (value != null) {
                this.addError("Error during " + MY_TYPE_NAME + " processing: " + TARGET_CLASS_NAME + " only allowed at class level.", parent);
                return;
            }
            if (parent instanceof MethodNode) {
                this.visitMethodNode((MethodNode)parent);
            } else if (parent instanceof FieldNode) {
                this.visitFieldNode((FieldNode)parent);
            }
        }
    }

    private void visitMethodNode(MethodNode methodNode) {
        if (methodNode.isSyntheticPublic()) {
            this.revertVisibility(methodNode);
        } else {
            this.addError("Can't use " + MY_TYPE_NAME + " for method '" + methodNode.getName() + "' which has explicit visibility.", methodNode);
        }
    }

    private void visitClassNode(ClassNode cNode, List<PackageScopeTarget> value) {
        String cName = cNode.getName();
        if (cNode.isInterface() && value.size() != 1 && value.get(0) != PackageScopeTarget.CLASS) {
            this.addError("Error processing interface '" + cName + "'. " + MY_TYPE_NAME + " not allowed for interfaces except when targeting Class level.", cNode);
        }
        if (value.contains((Object)PackageScopeTarget.CLASS)) {
            if (cNode.isSyntheticPublic()) {
                this.revertVisibility(cNode);
            } else {
                this.addError("Can't use " + MY_TYPE_NAME + " for class '" + cNode.getName() + "' which has explicit visibility.", cNode);
            }
        }
        if (value.contains((Object)PackageScopeTarget.METHODS)) {
            List<MethodNode> mList = cNode.getMethods();
            for (MethodNode methodNode : mList) {
                if (!methodNode.isSyntheticPublic()) continue;
                this.revertVisibility(methodNode);
            }
        }
        if (value.contains((Object)PackageScopeTarget.CONSTRUCTORS)) {
            List<ConstructorNode> cList = cNode.getDeclaredConstructors();
            for (MethodNode methodNode : cList) {
                if (!methodNode.isSyntheticPublic()) continue;
                this.revertVisibility(methodNode);
            }
        }
        if (value.contains((Object)PackageScopeTarget.FIELDS)) {
            List<PropertyNode> pList = cNode.getProperties();
            ArrayList<PropertyNode> foundProps = new ArrayList<PropertyNode>();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (PropertyNode pNode : pList) {
                foundProps.add(pNode);
                arrayList.add(pNode.getName());
            }
            for (PropertyNode pNode : foundProps) {
                pList.remove(pNode);
            }
            List<FieldNode> fList = cNode.getFields();
            for (FieldNode fNode : fList) {
                if (!arrayList.contains(fNode.getName())) continue;
                this.revertVisibility(fNode);
            }
        }
    }

    private void visitFieldNode(FieldNode fNode) {
        ClassNode cNode = fNode.getDeclaringClass();
        List<PropertyNode> pList = cNode.getProperties();
        PropertyNode foundProp = null;
        for (PropertyNode pNode : pList) {
            if (!pNode.getName().equals(fNode.getName())) continue;
            foundProp = pNode;
            break;
        }
        if (foundProp != null) {
            this.revertVisibility(fNode);
            pList.remove(foundProp);
        }
    }

    private void revertVisibility(FieldNode fNode) {
        fNode.setModifiers(fNode.getModifiers() & 0xFFFFFFFD);
    }

    private void revertVisibility(MethodNode mNode) {
        mNode.setModifiers(mNode.getModifiers() & 0xFFFFFFFE);
    }

    private void revertVisibility(ClassNode cNode) {
        cNode.setModifiers(cNode.getModifiers() & 0xFFFFFFFE);
    }

    private List<PackageScopeTarget> determineTargets(Expression expr) {
        ArrayList<PackageScopeTarget> list = new ArrayList<PackageScopeTarget>();
        if (expr instanceof PropertyExpression) {
            list.add(this.extractTarget((PropertyExpression)expr));
        } else if (expr instanceof ListExpression) {
            ListExpression expressionList = (ListExpression)expr;
            List<Expression> expressions = expressionList.getExpressions();
            for (Expression ex : expressions) {
                if (!(ex instanceof PropertyExpression)) continue;
                list.add(this.extractTarget((PropertyExpression)ex));
            }
        }
        return list;
    }

    private PackageScopeTarget extractTarget(PropertyExpression expr) {
        Expression prop;
        ClassExpression ce;
        Expression oe = expr.getObjectExpression();
        if (oe instanceof ClassExpression && (ce = (ClassExpression)oe).getType().getName().equals("groovy.transform.PackageScopeTarget") && (prop = expr.getProperty()) instanceof ConstantExpression) {
            String propName = (String)((ConstantExpression)prop).getValue();
            try {
                return PackageScopeTarget.valueOf(propName);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        throw new GroovyBugError("Internal error during " + MY_TYPE_NAME + " processing. Annotation parameters must be of type: " + TARGET_CLASS_NAME + ".");
    }
}

