/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.AutoClone;
import groovy.transform.AutoCloneStyle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class AutoCloneASTTransformation
extends AbstractASTTransformation {
    static final Class MY_CLASS = AutoClone.class;
    static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode CLONEABLE_TYPE = ClassHelper.make(Cloneable.class);
    private static final ClassNode BAOS_TYPE = ClassHelper.make(ByteArrayOutputStream.class);
    private static final ClassNode BAIS_TYPE = ClassHelper.make(ByteArrayInputStream.class);
    private static final ClassNode OOS_TYPE = ClassHelper.make(ObjectOutputStream.class);
    private static final ClassNode OIS_TYPE = ClassHelper.make(ObjectInputStream.class);
    private static final ClassNode INVOKER_TYPE = ClassHelper.make(InvokerHelper.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode) {
            ClassNode cNode = (ClassNode)parent;
            if (!this.checkNotInterface(cNode, MY_TYPE_NAME)) {
                return;
            }
            cNode.addInterface(CLONEABLE_TYPE);
            boolean includeFields = this.memberHasValue(anno, "includeFields", true);
            AutoCloneStyle style = this.getStyle(anno, "style");
            List<String> excludes = AutoCloneASTTransformation.getMemberList(anno, "excludes");
            List<FieldNode> list = GeneralUtils.getInstancePropertyFields(cNode);
            if (includeFields) {
                list.addAll(GeneralUtils.getInstanceNonPropertyFields(cNode));
            }
            if (style == null) {
                style = AutoCloneStyle.CLONE;
            }
            switch (style) {
                case COPY_CONSTRUCTOR: {
                    this.createCloneCopyConstructor(cNode, list, excludes);
                    break;
                }
                case SERIALIZATION: {
                    this.createCloneSerialization(cNode);
                    break;
                }
                case CLONE: {
                    this.createClone(cNode, list, excludes);
                    break;
                }
                case SIMPLE: {
                    this.createSimpleClone(cNode, list, excludes);
                }
            }
        }
    }

    private void createCloneSerialization(ClassNode cNode) {
        BlockStatement body = new BlockStatement();
        VariableExpression baos = GeneralUtils.varX("baos");
        body.addStatement(GeneralUtils.declS(baos, GeneralUtils.ctorX(BAOS_TYPE)));
        MethodCallExpression writeObject = GeneralUtils.callX((Expression)GeneralUtils.castX(OOS_TYPE, GeneralUtils.varX("it")), "writeObject", (Expression)GeneralUtils.varX("this"));
        writeObject.setImplicitThis(false);
        ClosureExpression writeClos = GeneralUtils.closureX(GeneralUtils.block(GeneralUtils.stmt(writeObject)));
        writeClos.setVariableScope(new VariableScope());
        body.addStatement(GeneralUtils.stmt(GeneralUtils.callX((Expression)baos, "withObjectOutputStream", (Expression)GeneralUtils.args(writeClos))));
        VariableExpression bais = GeneralUtils.varX("bais");
        body.addStatement(GeneralUtils.declS(bais, GeneralUtils.ctorX(BAIS_TYPE, GeneralUtils.args(GeneralUtils.callX(baos, "toByteArray")))));
        MethodCallExpression readObject = GeneralUtils.callX(GeneralUtils.castX(OIS_TYPE, GeneralUtils.varX("it")), "readObject");
        readObject.setImplicitThis(false);
        ClosureExpression readClos = GeneralUtils.closureX(GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.castX(GenericsUtils.nonGeneric(cNode), readObject))));
        readClos.setVariableScope(new VariableScope());
        MethodCallExpression classLoader = GeneralUtils.callX(GeneralUtils.callThisX("getClass"), "getClassLoader");
        body.addStatement(GeneralUtils.returnS(GeneralUtils.callX((Expression)bais, "withObjectInputStream", (Expression)GeneralUtils.args(classLoader, readClos))));
        new VariableScopeVisitor(this.sourceUnit, true).visitClass(cNode);
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(CloneNotSupportedException.class)};
        cNode.addMethod("clone", 1, GenericsUtils.nonGeneric(cNode), Parameter.EMPTY_ARRAY, exceptions, body);
    }

    private void createCloneCopyConstructor(ClassNode cNode, List<FieldNode> list, List<String> excludes) {
        if (cNode.getDeclaredConstructors().isEmpty()) {
            BlockStatement noArgBody = new BlockStatement();
            noArgBody.addStatement(EmptyStatement.INSTANCE);
            cNode.addConstructor(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, noArgBody);
        }
        boolean hasThisCons = false;
        for (ConstructorNode consNode : cNode.getDeclaredConstructors()) {
            Parameter[] parameters = consNode.getParameters();
            if (parameters.length != 1 || !parameters[0].getType().equals(cNode)) continue;
            hasThisCons = true;
        }
        if (!hasThisCons) {
            boolean hasParent;
            BlockStatement initBody = new BlockStatement();
            Parameter initParam = GeneralUtils.param(GenericsUtils.nonGeneric(cNode), "other");
            VariableExpression other = GeneralUtils.varX(initParam);
            boolean bl = hasParent = cNode.getSuperClass() != ClassHelper.OBJECT_TYPE;
            if (hasParent) {
                initBody.addStatement(GeneralUtils.stmt(GeneralUtils.ctorX(ClassNode.SUPER, other)));
            }
            for (FieldNode fieldNode : list) {
                String name = fieldNode.getName();
                if (excludes.contains(name)) continue;
                ClassNode fieldType = fieldNode.getType();
                Expression direct = GeneralUtils.propX((Expression)other, name);
                Expression to = GeneralUtils.propX((Expression)GeneralUtils.varX("this"), name);
                Statement assignDirect = GeneralUtils.assignS(to, direct);
                Statement assignCloned = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDirectX(direct)));
                Statement assignClonedDynamic = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDynamicX(direct)));
                if (this.isCloneableType(fieldType)) {
                    initBody.addStatement(assignCloned);
                    continue;
                }
                if (!this.possiblyCloneable(fieldType)) {
                    initBody.addStatement(assignDirect);
                    continue;
                }
                initBody.addStatement(GeneralUtils.ifElseS(GeneralUtils.isInstanceOfX(direct, CLONEABLE_TYPE), assignClonedDynamic, assignDirect));
            }
            cNode.addConstructor(4, GeneralUtils.params(initParam), ClassNode.EMPTY_ARRAY, initBody);
        }
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(CloneNotSupportedException.class)};
        cNode.addMethod("clone", 1, GenericsUtils.nonGeneric(cNode), Parameter.EMPTY_ARRAY, exceptions, GeneralUtils.block(GeneralUtils.stmt(GeneralUtils.ctorX(cNode, GeneralUtils.args(GeneralUtils.varX("this"))))));
    }

    private boolean isCloneableType(ClassNode fieldType) {
        return GeneralUtils.isOrImplements(fieldType, CLONEABLE_TYPE) || !fieldType.getAnnotations(MY_TYPE).isEmpty();
    }

    private boolean possiblyCloneable(ClassNode type) {
        return !ClassHelper.isPrimitiveType(type) && (this.isCloneableType(type) || (type.getModifiers() & 0x10) == 0);
    }

    private Expression callCloneDynamicX(Expression target) {
        return GeneralUtils.callX(INVOKER_TYPE, "invokeMethod", (Expression)GeneralUtils.args(target, GeneralUtils.constX("clone"), ConstantExpression.NULL));
    }

    private Expression callCloneDirectX(Expression direct) {
        return GeneralUtils.ternaryX(GeneralUtils.equalsNullX(direct), ConstantExpression.NULL, GeneralUtils.callX(direct, "clone"));
    }

    private void createSimpleClone(ClassNode cNode, List<FieldNode> fieldNodes, List<String> excludes) {
        if (cNode.getDeclaredConstructors().isEmpty()) {
            cNode.addConstructor(1, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.block(EmptyStatement.INSTANCE));
        }
        this.addSimpleCloneHelperMethod(cNode, fieldNodes, excludes);
        VariableExpression result = GeneralUtils.varX("_result", cNode);
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(CloneNotSupportedException.class)};
        cNode.addMethod("clone", 1, GenericsUtils.nonGeneric(cNode), Parameter.EMPTY_ARRAY, exceptions, GeneralUtils.block(GeneralUtils.declS(result, GeneralUtils.ctorX(cNode)), GeneralUtils.stmt(GeneralUtils.callThisX("cloneOrCopyMembers", GeneralUtils.args(result))), GeneralUtils.returnS(result)));
    }

    private void addSimpleCloneHelperMethod(ClassNode cNode, List<FieldNode> fieldNodes, List<String> excludes) {
        Parameter methodParam = new Parameter(GenericsUtils.nonGeneric(cNode), "other");
        VariableExpression other = GeneralUtils.varX(methodParam);
        boolean hasParent = cNode.getSuperClass() != ClassHelper.OBJECT_TYPE;
        BlockStatement methodBody = new BlockStatement();
        if (hasParent) {
            methodBody.addStatement(GeneralUtils.stmt(GeneralUtils.callSuperX("cloneOrCopyMembers", GeneralUtils.args(other))));
        }
        for (FieldNode fieldNode : fieldNodes) {
            String name = fieldNode.getName();
            if (excludes.contains(name)) continue;
            ClassNode fieldType = fieldNode.getType();
            Expression direct = GeneralUtils.propX((Expression)GeneralUtils.varX("this"), name);
            Expression to = GeneralUtils.propX((Expression)other, name);
            Statement assignDirect = GeneralUtils.assignS(to, direct);
            Statement assignCloned = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDirectX(direct)));
            Statement assignClonedDynamic = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDynamicX(direct)));
            if (this.isCloneableType(fieldType)) {
                methodBody.addStatement(assignCloned);
                continue;
            }
            if (!this.possiblyCloneable(fieldType)) {
                methodBody.addStatement(assignDirect);
                continue;
            }
            methodBody.addStatement(GeneralUtils.ifElseS(GeneralUtils.isInstanceOfX(direct, CLONEABLE_TYPE), assignClonedDynamic, assignDirect));
        }
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(CloneNotSupportedException.class)};
        cNode.addMethod("cloneOrCopyMembers", 4, ClassHelper.VOID_TYPE, GeneralUtils.params(methodParam), exceptions, methodBody);
    }

    private void createClone(ClassNode cNode, List<FieldNode> fieldNodes, List<String> excludes) {
        BlockStatement body = new BlockStatement();
        VariableExpression result = GeneralUtils.varX("_result", cNode);
        body.addStatement(GeneralUtils.declS(result, GeneralUtils.castX(cNode, GeneralUtils.callSuperX("clone"))));
        for (FieldNode fieldNode : fieldNodes) {
            if (excludes.contains(fieldNode.getName())) continue;
            ClassNode fieldType = fieldNode.getType();
            VariableExpression fieldExpr = GeneralUtils.varX(fieldNode);
            Expression to = GeneralUtils.propX((Expression)result, fieldNode.getName());
            Statement doClone = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDirectX(fieldExpr)));
            Statement doCloneDynamic = GeneralUtils.assignS(to, GeneralUtils.castX(fieldType, this.callCloneDynamicX(fieldExpr)));
            if (this.isCloneableType(fieldType)) {
                body.addStatement(doClone);
                continue;
            }
            if (!this.possiblyCloneable(fieldType)) continue;
            body.addStatement(GeneralUtils.ifS((Expression)GeneralUtils.isInstanceOfX(fieldExpr, CLONEABLE_TYPE), doCloneDynamic));
        }
        body.addStatement(GeneralUtils.returnS(result));
        ClassNode[] exceptions = new ClassNode[]{ClassHelper.make(CloneNotSupportedException.class)};
        cNode.addMethod("clone", 1, GenericsUtils.nonGeneric(cNode), Parameter.EMPTY_ARRAY, exceptions, body);
    }

    private AutoCloneStyle getStyle(AnnotationNode node, String name) {
        ClassExpression ce;
        PropertyExpression prop;
        Expression oe;
        Expression member = node.getMember(name);
        if (member != null && member instanceof PropertyExpression && (oe = (prop = (PropertyExpression)member).getObjectExpression()) instanceof ClassExpression && (ce = (ClassExpression)oe).getType().getName().equals("groovy.transform.AutoCloneStyle")) {
            return AutoCloneStyle.valueOf(prop.getPropertyAsString());
        }
        return null;
    }
}

