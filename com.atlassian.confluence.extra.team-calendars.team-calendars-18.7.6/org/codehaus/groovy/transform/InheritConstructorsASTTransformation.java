/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.InheritConstructors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class InheritConstructorsASTTransformation
extends AbstractASTTransformation {
    private static final Class MY_CLASS = InheritConstructors.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            this.processClass((ClassNode)parent, node);
        }
    }

    private void processClass(ClassNode cNode, AnnotationNode node) {
        if (cNode.isInterface()) {
            this.addError("Error processing interface '" + cNode.getName() + "'. " + MY_TYPE_NAME + " only allowed for classes.", cNode);
            return;
        }
        boolean copyConstructorAnnotations = this.memberHasValue(node, "constructorAnnotations", true);
        boolean copyParameterAnnotations = this.memberHasValue(node, "parameterAnnotations", true);
        ClassNode sNode = cNode.getSuperClass();
        List<AnnotationNode> superAnnotations = sNode.getAnnotations(MY_TYPE);
        if (superAnnotations.size() == 1) {
            this.processClass(sNode, node);
        }
        for (ConstructorNode cn : sNode.getDeclaredConstructors()) {
            this.addConstructorUnlessAlreadyExisting(cNode, cn, copyConstructorAnnotations, copyParameterAnnotations);
        }
    }

    private void addConstructorUnlessAlreadyExisting(ClassNode classNode, ConstructorNode consNode, boolean copyConstructorAnnotations, boolean copyParameterAnnotations) {
        Parameter[] origParams = consNode.getParameters();
        if (consNode.isPrivate()) {
            return;
        }
        Parameter[] params = new Parameter[origParams.length];
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(classNode);
        GenericsUtils.extractSuperClassGenerics(classNode, classNode.getSuperClass(), genericsSpec);
        List<Expression> theArgs = this.buildParams(origParams, params, genericsSpec, copyParameterAnnotations);
        if (InheritConstructorsASTTransformation.isExisting(classNode, params)) {
            return;
        }
        ConstructorNode added = classNode.addConstructor(consNode.getModifiers(), params, consNode.getExceptions(), GeneralUtils.block(GeneralUtils.ctorSuperS(GeneralUtils.args(theArgs))));
        if (copyConstructorAnnotations) {
            added.addAnnotations(this.copyAnnotatedNodeAnnotations(consNode, MY_TYPE_NAME));
        }
    }

    private List<Expression> buildParams(Parameter[] origParams, Parameter[] params, Map<String, ClassNode> genericsSpec, boolean copyParameterAnnotations) {
        ArrayList<Expression> theArgs = new ArrayList<Expression>();
        for (int i = 0; i < origParams.length; ++i) {
            Parameter p = origParams[i];
            ClassNode newType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, p.getType());
            Parameter parameter = params[i] = p.hasInitialExpression() ? GeneralUtils.param(newType, p.getName(), p.getInitialExpression()) : GeneralUtils.param(newType, p.getName());
            if (copyParameterAnnotations) {
                params[i].addAnnotations(this.copyAnnotatedNodeAnnotations(origParams[i], MY_TYPE_NAME));
            }
            theArgs.add(GeneralUtils.varX(p.getName(), newType));
        }
        return theArgs;
    }

    private static boolean isExisting(ClassNode classNode, Parameter[] params) {
        for (ConstructorNode consNode : classNode.getDeclaredConstructors()) {
            if (!InheritConstructorsASTTransformation.matchingTypes(params, consNode.getParameters())) continue;
            return true;
        }
        return false;
    }

    private static boolean matchingTypes(Parameter[] params, Parameter[] existingParams) {
        return MopWriter.equalParameterTypes(params, existingParams);
    }
}

