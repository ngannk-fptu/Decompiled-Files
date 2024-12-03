/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class SingletonASTTransformation
extends AbstractASTTransformation {
    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (parent instanceof ClassNode) {
            ClassNode classNode = (ClassNode)parent;
            String propertyName = SingletonASTTransformation.getMemberStringValue(node, "property", "instance");
            boolean isLazy = this.memberHasValue(node, "lazy", true);
            boolean isStrict = !this.memberHasValue(node, "strict", false);
            this.createField(classNode, propertyName, isLazy, isStrict);
        }
    }

    private void createField(ClassNode classNode, String propertyName, boolean isLazy, boolean isStrict) {
        int modifiers = isLazy ? 74 : 25;
        ConstructorCallExpression initialValue = isLazy ? null : GeneralUtils.ctorX(classNode);
        FieldNode fieldNode = classNode.addField(propertyName, modifiers, GenericsUtils.newClass(classNode), initialValue);
        this.createConstructor(classNode, fieldNode, propertyName, isStrict);
        BlockStatement body = new BlockStatement();
        body.addStatement(isLazy ? this.lazyBody(classNode, fieldNode) : this.nonLazyBody(fieldNode));
        classNode.addMethod(this.getGetterName(propertyName), 9, GenericsUtils.newClass(classNode), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body);
    }

    private Statement nonLazyBody(FieldNode fieldNode) {
        return GeneralUtils.returnS(GeneralUtils.varX(fieldNode));
    }

    private Statement lazyBody(ClassNode classNode, FieldNode fieldNode) {
        VariableExpression instanceExpression = GeneralUtils.varX(fieldNode);
        return GeneralUtils.ifElseS(GeneralUtils.notNullX(instanceExpression), GeneralUtils.returnS(instanceExpression), new SynchronizedStatement(GeneralUtils.classX(classNode), GeneralUtils.ifElseS(GeneralUtils.notNullX(instanceExpression), GeneralUtils.returnS(instanceExpression), GeneralUtils.returnS(GeneralUtils.assignX(instanceExpression, GeneralUtils.ctorX(classNode))))));
    }

    private String getGetterName(String propertyName) {
        return "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    private void createConstructor(ClassNode classNode, FieldNode field, String propertyName, boolean isStrict) {
        List<ConstructorNode> cNodes = classNode.getDeclaredConstructors();
        ConstructorNode foundNoArg = null;
        for (ConstructorNode cNode : cNodes) {
            Parameter[] parameters = cNode.getParameters();
            if (parameters != null && parameters.length != 0) continue;
            foundNoArg = cNode;
            break;
        }
        if (isStrict && !cNodes.isEmpty()) {
            for (ConstructorNode cNode : cNodes) {
                this.addError("@Singleton didn't expect to find one or more additional constructors: remove constructor(s) or set strict=false", cNode);
            }
        }
        if (foundNoArg == null) {
            BlockStatement body = new BlockStatement();
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.notNullX(GeneralUtils.varX(field)), GeneralUtils.throwS(GeneralUtils.ctorX(ClassHelper.make(RuntimeException.class), GeneralUtils.args(GeneralUtils.constX("Can't instantiate singleton " + classNode.getName() + ". Use " + classNode.getName() + "." + propertyName))))));
            classNode.addConstructor(new ConstructorNode(2, body));
        }
    }
}

