/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.BaseScript;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class BaseScriptASTTransformation
extends AbstractASTTransformation {
    private static final Class<BaseScript> MY_CLASS = BaseScript.class;
    public static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final Parameter[] CONTEXT_CTOR_PARAMETERS = new Parameter[]{new Parameter(ClassHelper.BINDING_TYPE, "context")};

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        if (parent instanceof DeclarationExpression) {
            this.changeBaseScriptTypeFromDeclaration((DeclarationExpression)parent, node);
        } else if (parent instanceof ImportNode || parent instanceof PackageNode) {
            this.changeBaseScriptTypeFromPackageOrImport(source, parent, node);
        } else if (parent instanceof ClassNode) {
            this.changeBaseScriptTypeFromClass((ClassNode)parent, node);
        }
    }

    private void changeBaseScriptTypeFromPackageOrImport(SourceUnit source, AnnotatedNode parent, AnnotationNode node) {
        Expression value = node.getMember("value");
        if (!(value instanceof ClassExpression)) {
            this.addError("Annotation " + MY_TYPE_NAME + " member 'value' should be a class literal.", value);
            return;
        }
        List<ClassNode> classes = source.getAST().getClasses();
        for (ClassNode classNode : classes) {
            if (!classNode.isScriptBody()) continue;
            this.changeBaseScriptType(parent, classNode, value.getType());
        }
    }

    private void changeBaseScriptTypeFromClass(ClassNode parent, AnnotationNode node) {
        this.changeBaseScriptType(parent, parent, parent.getSuperClass());
    }

    private void changeBaseScriptTypeFromDeclaration(DeclarationExpression de, AnnotationNode node) {
        if (de.isMultipleAssignmentDeclaration()) {
            this.addError("Annotation " + MY_TYPE_NAME + " not supported with multiple assignment notation.", de);
            return;
        }
        if (!(de.getRightExpression() instanceof EmptyExpression)) {
            this.addError("Annotation " + MY_TYPE_NAME + " not supported with variable assignment.", de);
            return;
        }
        Expression value = node.getMember("value");
        if (value != null) {
            this.addError("Annotation " + MY_TYPE_NAME + " cannot have member 'value' if used on a declaration.", value);
            return;
        }
        ClassNode cNode = de.getDeclaringClass();
        ClassNode baseScriptType = de.getVariableExpression().getType().getPlainNodeReference();
        de.setRightExpression(new VariableExpression("this"));
        this.changeBaseScriptType(de, cNode, baseScriptType);
    }

    private void changeBaseScriptType(AnnotatedNode parent, ClassNode cNode, ClassNode baseScriptType) {
        MethodNode defaultMethod;
        if (!cNode.isScriptBody()) {
            this.addError("Annotation " + MY_TYPE_NAME + " can only be used within a Script.", parent);
            return;
        }
        if (!baseScriptType.isScript()) {
            this.addError("Declared type " + baseScriptType + " does not extend groovy.lang.Script class!", parent);
            return;
        }
        cNode.setSuperClass(baseScriptType);
        MethodNode runScriptMethod = ClassHelper.findSAM(baseScriptType);
        if (this.isCustomScriptBodyMethod(runScriptMethod) && (defaultMethod = cNode.getDeclaredMethod("run", Parameter.EMPTY_ARRAY)) != null) {
            cNode.removeMethod(defaultMethod);
            MethodNode methodNode = new MethodNode(runScriptMethod.getName(), runScriptMethod.getModifiers() & 0xFFFFFBFF, runScriptMethod.getReturnType(), runScriptMethod.getParameters(), runScriptMethod.getExceptions(), defaultMethod.getCode());
            methodNode.copyNodeMetaData(defaultMethod);
            cNode.addMethod(methodNode);
        }
        if (cNode.getSuperClass().getDeclaredConstructor(CONTEXT_CTOR_PARAMETERS) == null) {
            ConstructorNode orphanedConstructor = cNode.getDeclaredConstructor(CONTEXT_CTOR_PARAMETERS);
            cNode.removeConstructor(orphanedConstructor);
        }
    }

    private boolean isCustomScriptBodyMethod(MethodNode node) {
        return node != null && (!node.getDeclaringClass().equals(ClassHelper.SCRIPT_TYPE) || !"run".equals(node.getName()) || node.getParameters().length != 0);
    }
}

