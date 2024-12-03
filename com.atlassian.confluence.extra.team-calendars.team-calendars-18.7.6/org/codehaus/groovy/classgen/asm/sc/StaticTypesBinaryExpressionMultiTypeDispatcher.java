/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.asm.BinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.BinaryExpressionWriter;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.VariableSlotLoader;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticInvocationWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticPropertyAccessHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticTypesBinaryExpressionMultiTypeDispatcher
extends BinaryExpressionMultiTypeDispatcher
implements Opcodes {
    private final AtomicInteger labelCounter = new AtomicInteger();
    private static final MethodNode CLOSURE_GETTHISOBJECT_METHOD = ClassHelper.CLOSURE_TYPE.getMethod("getThisObject", new Parameter[0]);

    public StaticTypesBinaryExpressionMultiTypeDispatcher(WriterController wc) {
        super(wc);
    }

    @Override
    protected void writePostOrPrefixMethod(int op, String method, Expression expression, Expression orig) {
        MethodNode mn = (MethodNode)orig.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        WriterController controller = this.getController();
        OperandStack operandStack = controller.getOperandStack();
        if (mn != null) {
            operandStack.pop();
            MethodCallExpression call = new MethodCallExpression(expression, method, (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            call.setMethodTarget(mn);
            call.visit(controller.getAcg());
            return;
        }
        ClassNode top = operandStack.getTopOperand();
        if (ClassHelper.isPrimitiveType(top) && (ClassHelper.isNumberType(top) || ClassHelper.char_TYPE.equals(top))) {
            MethodVisitor mv = controller.getMethodVisitor();
            if (WideningCategories.isIntCategory(top) || ClassHelper.char_TYPE.equals(top)) {
                mv.visitInsn(4);
            } else if (ClassHelper.long_TYPE.equals(top)) {
                mv.visitInsn(10);
            } else if (ClassHelper.float_TYPE.equals(top)) {
                mv.visitInsn(12);
            } else if (ClassHelper.double_TYPE.equals(top)) {
                mv.visitInsn(15);
            }
            if ("next".equals(method)) {
                if (WideningCategories.isIntCategory(top) || ClassHelper.char_TYPE.equals(top)) {
                    mv.visitInsn(96);
                } else if (ClassHelper.long_TYPE.equals(top)) {
                    mv.visitInsn(97);
                } else if (ClassHelper.float_TYPE.equals(top)) {
                    mv.visitInsn(98);
                } else if (ClassHelper.double_TYPE.equals(top)) {
                    mv.visitInsn(99);
                }
            } else if (WideningCategories.isIntCategory(top) || ClassHelper.char_TYPE.equals(top)) {
                mv.visitInsn(100);
            } else if (ClassHelper.long_TYPE.equals(top)) {
                mv.visitInsn(101);
            } else if (ClassHelper.float_TYPE.equals(top)) {
                mv.visitInsn(102);
            } else if (ClassHelper.double_TYPE.equals(top)) {
                mv.visitInsn(103);
            }
            return;
        }
        super.writePostOrPrefixMethod(op, method, expression, orig);
    }

    @Override
    public void evaluateEqual(BinaryExpression expression, boolean defineVariable) {
        PropertyExpression pexp;
        Expression leftExpression;
        if (!defineVariable && (leftExpression = expression.getLeftExpression()) instanceof PropertyExpression && this.makeSetProperty((pexp = (PropertyExpression)leftExpression).getObjectExpression(), pexp.getProperty(), expression.getRightExpression(), pexp.isSafe(), pexp.isSpreadSafe(), pexp.isImplicitThis(), pexp instanceof AttributeExpression)) {
            return;
        }
        if (expression.getLeftExpression() instanceof PropertyExpression && ((PropertyExpression)expression.getLeftExpression()).isSpreadSafe() && StaticTypeCheckingSupport.isAssignment(expression.getOperation().getType())) {
            this.transformSpreadOnLHS(expression);
            return;
        }
        super.evaluateEqual(expression, defineVariable);
    }

    private void transformSpreadOnLHS(BinaryExpression origin) {
        PropertyExpression spreadExpression = (PropertyExpression)origin.getLeftExpression();
        Expression value = origin.getRightExpression();
        WriterController controller = this.getController();
        MethodVisitor mv = controller.getMethodVisitor();
        CompileStack compileStack = controller.getCompileStack();
        TypeChooser typeChooser = controller.getTypeChooser();
        OperandStack operandStack = controller.getOperandStack();
        ClassNode classNode = controller.getClassNode();
        int counter = this.labelCounter.incrementAndGet();
        Expression receiver = spreadExpression.getObjectExpression();
        VariableExpression result = new VariableExpression(this.getClass().getSimpleName() + "$spreadresult" + counter, StaticCompilationVisitor.ARRAYLIST_CLASSNODE);
        ConstructorCallExpression cce = new ConstructorCallExpression(StaticCompilationVisitor.ARRAYLIST_CLASSNODE, ArgumentListExpression.EMPTY_ARGUMENTS);
        cce.setNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, StaticCompilationVisitor.ARRAYLIST_CONSTRUCTOR);
        DeclarationExpression declr = new DeclarationExpression(result, Token.newSymbol("=", spreadExpression.getLineNumber(), spreadExpression.getColumnNumber()), (Expression)cce);
        declr.visit(controller.getAcg());
        receiver.visit(controller.getAcg());
        Label ifnull = compileStack.createLocalLabel("ifnull_" + counter);
        mv.visitJumpInsn(198, ifnull);
        operandStack.remove(1);
        Label nonull = compileStack.createLocalLabel("nonull_" + counter);
        mv.visitLabel(nonull);
        ClassNode componentType = StaticTypeCheckingVisitor.inferLoopElementType(typeChooser.resolveType(receiver, classNode));
        Parameter iterator = new Parameter(componentType, "for$it$" + counter);
        VariableExpression iteratorAsVar = new VariableExpression(iterator);
        PropertyExpression pexp = spreadExpression instanceof AttributeExpression ? new AttributeExpression(iteratorAsVar, spreadExpression.getProperty(), true) : new PropertyExpression(iteratorAsVar, spreadExpression.getProperty(), true);
        pexp.setImplicitThis(spreadExpression.isImplicitThis());
        pexp.setSourcePosition(spreadExpression);
        BinaryExpression assignment = new BinaryExpression(pexp, origin.getOperation(), value);
        MethodCallExpression add = new MethodCallExpression((Expression)result, "add", (Expression)assignment);
        add.setMethodTarget(StaticCompilationVisitor.ARRAYLIST_ADD_METHOD);
        ForStatement stmt = new ForStatement(iterator, receiver, new ExpressionStatement(add));
        stmt.visit(controller.getAcg());
        mv.visitLabel(ifnull);
        result.visit(controller.getAcg());
    }

    private boolean makeSetProperty(Expression receiver, Expression message, Expression arguments, boolean safe, boolean spreadSafe, boolean implicitThis, boolean isAttribute) {
        boolean isThisExpression;
        WriterController controller = this.getController();
        TypeChooser typeChooser = controller.getTypeChooser();
        ClassNode receiverType = typeChooser.resolveType(receiver, controller.getClassNode());
        String property = message.getText();
        boolean bl = isThisExpression = receiver instanceof VariableExpression && ((VariableExpression)receiver).isThisExpression();
        if (isAttribute || isThisExpression && receiverType.getDeclaredField(property) != null) {
            ClassNode current = receiverType;
            FieldNode fn = null;
            while (fn == null && current != null) {
                fn = current.getDeclaredField(property);
                if (fn != null) continue;
                current = current.getSuperClass();
            }
            if (fn != null && receiverType != current && !fn.isPublic()) {
                String pkg2;
                if (!fn.isProtected()) {
                    return false;
                }
                String pkg1 = receiverType.getPackageName();
                if (pkg1 != (pkg2 = current.getPackageName()) && !pkg1.equals(pkg2)) {
                    return false;
                }
                OperandStack operandStack = controller.getOperandStack();
                MethodVisitor mv = controller.getMethodVisitor();
                if (!fn.isStatic()) {
                    receiver.visit(controller.getAcg());
                }
                arguments.visit(controller.getAcg());
                operandStack.doGroovyCast(fn.getOriginType());
                mv.visitFieldInsn(fn.isStatic() ? 179 : 181, BytecodeHelper.getClassInternalName(fn.getOwner()), property, BytecodeHelper.getTypeDescription(fn.getOriginType()));
                operandStack.remove(fn.isStatic() ? 1 : 2);
                return true;
            }
        }
        if (!isAttribute) {
            int mods;
            PropertyNode propertyNode;
            ClassNode declaringClass;
            String setter = "set" + MetaClassHelper.capitalize(property);
            MethodNode setterMethod = receiverType.getSetterMethod(setter, false);
            ClassNode classNode = declaringClass = setterMethod != null ? setterMethod.getDeclaringClass() : null;
            if (isThisExpression && declaringClass != null && declaringClass.equals(controller.getClassNode())) {
                setterMethod = null;
            } else if (setterMethod == null && (propertyNode = receiverType.getProperty(property)) != null && !Modifier.isFinal(mods = propertyNode.getModifiers())) {
                setterMethod = new MethodNode(setter, 1, ClassHelper.VOID_TYPE, new Parameter[]{new Parameter(propertyNode.getOriginType(), "value")}, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
                setterMethod.setDeclaringClass(receiverType);
            }
            if (setterMethod != null) {
                Expression call = StaticPropertyAccessHelper.transformToSetterCall(receiver, setterMethod, arguments, implicitThis, safe, spreadSafe, true, message);
                call.visit(controller.getAcg());
                return true;
            }
            if (isThisExpression && !controller.isInClosure()) {
                receiverType = controller.getClassNode();
            }
            if (this.makeSetPrivateFieldWithBridgeMethod(receiver, receiverType, property, arguments, safe, spreadSafe, implicitThis)) {
                return true;
            }
        }
        return false;
    }

    private boolean makeSetPrivateFieldWithBridgeMethod(Expression receiver, ClassNode receiverType, String fieldName, Expression arguments, boolean safe, boolean spreadSafe, boolean implicitThis) {
        MethodNode methodNode;
        Map mutators;
        WriterController controller = this.getController();
        FieldNode field = receiverType.getField(fieldName);
        ClassNode outerClass = receiverType.getOuterClass();
        if (field == null && implicitThis && outerClass != null && !receiverType.isStaticClass()) {
            Expression pexp;
            if (controller.isInClosure()) {
                MethodCallExpression mce = new MethodCallExpression((Expression)new VariableExpression("this"), "getThisObject", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
                mce.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, controller.getOutermostClass());
                mce.setImplicitThis(true);
                mce.setMethodTarget(CLOSURE_GETTHISOBJECT_METHOD);
                pexp = new CastExpression(controller.getOutermostClass(), mce);
            } else {
                pexp = new PropertyExpression((Expression)new ClassExpression(outerClass), "this");
                pexp.setImplicitThis(true);
            }
            pexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, outerClass);
            pexp.setSourcePosition(receiver);
            return this.makeSetPrivateFieldWithBridgeMethod(pexp, outerClass, fieldName, arguments, safe, spreadSafe, true);
        }
        ClassNode classNode = controller.getClassNode();
        if (field != null && Modifier.isPrivate(field.getModifiers()) && (StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(receiverType, classNode) || StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(classNode, receiverType)) && !receiverType.equals(classNode) && (mutators = (Map)receiverType.redirect().getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_FIELDS_MUTATORS)) != null && (methodNode = (MethodNode)mutators.get(fieldName)) != null) {
            MethodCallExpression mce = new MethodCallExpression(receiver, methodNode.getName(), (Expression)new ArgumentListExpression(field.isStatic() ? new ConstantExpression(null) : receiver, arguments));
            mce.setMethodTarget(methodNode);
            mce.setSafe(safe);
            mce.setSpreadSafe(spreadSafe);
            mce.setImplicitThis(implicitThis);
            mce.visit(controller.getAcg());
            return true;
        }
        return false;
    }

    @Override
    protected void assignToArray(Expression parent, Expression receiver, Expression index, Expression rhsValueLoader) {
        ClassNode current = this.getController().getClassNode();
        ClassNode arrayType = this.getController().getTypeChooser().resolveType(receiver, current);
        ClassNode arrayComponentType = arrayType.getComponentType();
        int operationType = this.getOperandType(arrayComponentType);
        BinaryExpressionWriter bew = this.binExpWriter[operationType];
        if (bew.arraySet(true) && arrayType.isArray()) {
            super.assignToArray(parent, receiver, index, rhsValueLoader);
        } else {
            WriterController controller = this.getController();
            StaticCompilationVisitor visitor = new StaticCompilationVisitor(controller.getSourceUnit(), controller.getClassNode());
            ArgumentListExpression ae = new ArgumentListExpression(index, rhsValueLoader);
            if (rhsValueLoader instanceof VariableSlotLoader && parent instanceof BinaryExpression) {
                rhsValueLoader.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, controller.getTypeChooser().resolveType(parent, controller.getClassNode()));
            }
            MethodCallExpression mce = new MethodCallExpression(receiver, "putAt", (Expression)ae);
            mce.setSourcePosition(parent);
            ((StaticTypeCheckingVisitor)visitor).visitMethodCallExpression(mce);
            OperandStack operandStack = controller.getOperandStack();
            int height = operandStack.getStackLength();
            mce.visit(controller.getAcg());
            operandStack.pop();
            operandStack.remove(operandStack.getStackLength() - height);
            rhsValueLoader.visit(controller.getAcg());
        }
    }
}

