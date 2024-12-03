/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.ExpressionAsVariableSlot;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.VariableSlotLoader;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesCallSiteWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterController;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.codehaus.groovy.transform.sc.TemporaryVariableExpression;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticInvocationWriter
extends InvocationWriter {
    private static final ClassNode INVOKERHELPER_CLASSNODE = ClassHelper.make(InvokerHelper.class);
    private static final Expression INVOKERHELER_RECEIVER = new ClassExpression(INVOKERHELPER_CLASSNODE);
    private static final MethodNode INVOKERHELPER_INVOKEMETHOD = INVOKERHELPER_CLASSNODE.getMethod("invokeMethodSafe", new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "object"), new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "args")});
    private static final MethodNode INVOKERHELPER_INVOKESTATICMETHOD = INVOKERHELPER_CLASSNODE.getMethod("invokeStaticMethod", new Parameter[]{new Parameter(ClassHelper.CLASS_Type, "clazz"), new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "args")});
    private final AtomicInteger labelCounter = new AtomicInteger();
    final WriterController controller;
    private MethodCallExpression currentCall;

    public StaticInvocationWriter(WriterController wc) {
        super(wc);
        this.controller = wc;
    }

    @Override
    protected boolean makeDirectCall(Expression origin, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean implicitThis, boolean containsSpreadExpression) {
        ClassNode superClass;
        if (origin instanceof MethodCallExpression && receiver instanceof VariableExpression && ((VariableExpression)receiver).isSuperExpression() && (superClass = (ClassNode)receiver.getNodeMetaData((Object)StaticCompilationMetadataKeys.PROPERTY_OWNER)) != null && !this.controller.getCompileStack().isLHS()) {
            MethodCallExpression mce = (MethodCallExpression)origin;
            MethodNode node = superClass.getDeclaredMethod(mce.getMethodAsString(), Parameter.EMPTY_ARRAY);
            mce.setMethodTarget(node);
        }
        return super.makeDirectCall(origin, receiver, message, arguments, adapter, implicitThis, containsSpreadExpression);
    }

    @Override
    public void writeInvokeMethod(MethodCallExpression call) {
        MethodCallExpression old = this.currentCall;
        this.currentCall = call;
        super.writeInvokeMethod(call);
        this.currentCall = old;
    }

    @Override
    public void writeInvokeConstructor(ConstructorCallExpression call) {
        ConstructorNode cn;
        MethodNode mn = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (mn == null) {
            super.writeInvokeConstructor(call);
            return;
        }
        if (this.writeAICCall(call)) {
            return;
        }
        if (mn instanceof ConstructorNode) {
            cn = (ConstructorNode)mn;
        } else {
            cn = new ConstructorNode(mn.getModifiers(), mn.getParameters(), mn.getExceptions(), mn.getCode());
            cn.setDeclaringClass(mn.getDeclaringClass());
        }
        ArgumentListExpression args = StaticInvocationWriter.makeArgumentList(call.getArguments());
        if (cn.isPrivate()) {
            ClassNode classNode = this.controller.getClassNode();
            ClassNode declaringClass = cn.getDeclaringClass();
            if (declaringClass != classNode) {
                MethodNode bridge = null;
                if (call.getNodeMetaData((Object)StaticTypesMarker.PV_METHODS_ACCESS) != null) {
                    Map bridgeMethods = (Map)declaringClass.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_BRIDGE_METHODS);
                    MethodNode methodNode = bridge = bridgeMethods != null ? (MethodNode)bridgeMethods.get(cn) : null;
                }
                if (bridge != null && bridge instanceof ConstructorNode) {
                    ArgumentListExpression newArgs = new ArgumentListExpression(new ConstantExpression(null));
                    for (Expression arg : args) {
                        newArgs.addExpression(arg);
                    }
                    cn = (ConstructorNode)bridge;
                    args = newArgs;
                } else {
                    this.controller.getSourceUnit().addError(new SyntaxException("Cannot call private constructor for " + declaringClass.toString(false) + " from class " + classNode.toString(false), call.getLineNumber(), call.getColumnNumber(), mn.getLastLineNumber(), call.getLastColumnNumber()));
                }
            }
        }
        String ownerDescriptor = this.prepareConstructorCall(cn);
        int before = this.controller.getOperandStack().getStackLength();
        this.loadArguments(args.getExpressions(), cn.getParameters());
        this.finnishConstructorCall(cn, ownerDescriptor, this.controller.getOperandStack().getStackLength() - before);
    }

    @Override
    public void writeSpecialConstructorCall(ConstructorCallExpression call) {
        ConstructorNode cn;
        MethodNode mn = (MethodNode)call.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (mn == null) {
            super.writeSpecialConstructorCall(call);
            return;
        }
        this.controller.getCompileStack().pushInSpecialConstructorCall();
        if (mn instanceof ConstructorNode) {
            cn = (ConstructorNode)mn;
        } else {
            cn = new ConstructorNode(mn.getModifiers(), mn.getParameters(), mn.getExceptions(), mn.getCode());
            cn.setDeclaringClass(mn.getDeclaringClass());
        }
        this.controller.getMethodVisitor().visitVarInsn(25, 0);
        String ownerDescriptor = BytecodeHelper.getClassInternalName(cn.getDeclaringClass());
        ArgumentListExpression args = StaticInvocationWriter.makeArgumentList(call.getArguments());
        int before = this.controller.getOperandStack().getStackLength();
        this.loadArguments(args.getExpressions(), cn.getParameters());
        this.finnishConstructorCall(cn, ownerDescriptor, this.controller.getOperandStack().getStackLength() - before);
        this.controller.getOperandStack().remove(1);
        this.controller.getCompileStack().pop();
    }

    protected boolean tryBridgeMethod(MethodNode target, Expression receiver, boolean implicitThis, TupleExpression args) {
        Map bridges;
        MethodNode bridge;
        ClassNode lookupClassNode;
        if (target.isProtected()) {
            lookupClassNode = this.controller.getClassNode();
            if (this.controller.isInClosure()) {
                lookupClassNode = lookupClassNode.getOuterClass();
            }
        } else {
            lookupClassNode = target.getDeclaringClass().redirect();
        }
        MethodNode methodNode = bridge = (bridges = (Map)lookupClassNode.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_BRIDGE_METHODS)) == null ? null : (MethodNode)bridges.get(target);
        if (bridge != null) {
            Expression fixedReceiver = receiver;
            ClassNode declaringClass = bridge.getDeclaringClass();
            if (implicitThis && !this.controller.isInClosure()) {
                ClassNode classNode = this.controller.getClassNode();
                while (!classNode.isDerivedFrom(declaringClass) && !classNode.implementsInterface(declaringClass) && classNode instanceof InnerClassNode) {
                    classNode = classNode.getOuterClass();
                }
                fixedReceiver = new PropertyExpression((Expression)new ClassExpression(classNode), "this");
            }
            ArgumentListExpression newArgs = new ArgumentListExpression(target.isStatic() ? new ConstantExpression(null) : fixedReceiver);
            for (Expression expression : args.getExpressions()) {
                newArgs.addExpression(expression);
            }
            return this.writeDirectMethodCall(bridge, implicitThis, fixedReceiver, newArgs);
        }
        return false;
    }

    @Override
    protected boolean writeDirectMethodCall(MethodNode target, boolean implicitThis, Expression receiver, TupleExpression args) {
        if (target == null) {
            return false;
        }
        if (target instanceof ExtensionMethodNode) {
            ExtensionMethodNode emn = (ExtensionMethodNode)target;
            MethodNode node = emn.getExtensionMethodNode();
            String methodName = target.getName();
            MethodVisitor mv = this.controller.getMethodVisitor();
            int argumentsToRemove = 0;
            LinkedList<Expression> argumentList = new LinkedList<Expression>(args.getExpressions());
            if (emn.isStaticExtension()) {
                argumentList.add(0, ConstantExpression.NULL);
            } else {
                argumentList.add(0, receiver);
            }
            Parameter[] parameters = node.getParameters();
            this.loadArguments(argumentList, parameters);
            String owner = BytecodeHelper.getClassInternalName(node.getDeclaringClass());
            String desc = BytecodeHelper.getMethodDescriptor(target.getReturnType(), parameters);
            mv.visitMethodInsn(184, owner, methodName, desc, false);
            ClassNode ret = target.getReturnType().redirect();
            if (ret == ClassHelper.VOID_TYPE) {
                ret = ClassHelper.OBJECT_TYPE;
                mv.visitInsn(1);
            }
            this.controller.getOperandStack().remove(argumentsToRemove += argumentList.size());
            this.controller.getOperandStack().push(ret);
            return true;
        }
        if (target == StaticTypeCheckingVisitor.CLOSURE_CALL_VARGS) {
            ArrayExpression arr = new ArrayExpression(ClassHelper.OBJECT_TYPE, args.getExpressions());
            return super.writeDirectMethodCall(target, implicitThis, receiver, new ArgumentListExpression(arr));
        }
        ClassNode classNode = this.controller.getClassNode();
        if (classNode.isDerivedFrom(ClassHelper.CLOSURE_TYPE) && this.controller.isInClosure() && !target.isPublic() && target.getDeclaringClass() != classNode) {
            if (!this.tryBridgeMethod(target, receiver, implicitThis, args)) {
                ArrayExpression arr = new ArrayExpression(ClassHelper.OBJECT_TYPE, args.getExpressions());
                MethodCallExpression mce = new MethodCallExpression(INVOKERHELER_RECEIVER, target.isStatic() ? "invokeStaticMethod" : "invokeMethodSafe", (Expression)new ArgumentListExpression(target.isStatic() ? new ClassExpression(target.getDeclaringClass()) : receiver, new ConstantExpression(target.getName()), arr));
                mce.setMethodTarget(target.isStatic() ? INVOKERHELPER_INVOKESTATICMETHOD : INVOKERHELPER_INVOKEMETHOD);
                mce.visit(this.controller.getAcg());
                return true;
            }
            return true;
        }
        if (target.isPrivate()) {
            ClassNode declaringClass = target.getDeclaringClass();
            if ((StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(declaringClass, classNode) || StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(classNode, declaringClass)) && declaringClass.getNodeMetaData((Object)StaticCompilationMetadataKeys.PRIVATE_BRIDGE_METHODS) != null && !declaringClass.equals(classNode)) {
                if (this.tryBridgeMethod(target, receiver, implicitThis, args)) {
                    return true;
                }
                if (declaringClass != classNode) {
                    this.controller.getSourceUnit().addError(new SyntaxException("Cannot call private method " + (target.isStatic() ? "static " : "") + declaringClass.toString(false) + "#" + target.getName() + " from class " + classNode.toString(false), receiver.getLineNumber(), receiver.getColumnNumber(), receiver.getLastLineNumber(), receiver.getLastColumnNumber()));
                }
            }
            if (declaringClass != classNode) {
                this.controller.getSourceUnit().addError(new SyntaxException("Cannot call private method " + (target.isStatic() ? "static " : "") + declaringClass.toString(false) + "#" + target.getName() + " from class " + classNode.toString(false), receiver.getLineNumber(), receiver.getColumnNumber(), receiver.getLastLineNumber(), receiver.getLastColumnNumber()));
            }
        }
        if (!(receiver == null || receiver instanceof VariableExpression && ((VariableExpression)receiver).isSuperExpression())) {
            CheckcastReceiverExpression checkCastReceiver = new CheckcastReceiverExpression(receiver, target);
            return super.writeDirectMethodCall(target, implicitThis, checkCastReceiver, args);
        }
        return super.writeDirectMethodCall(target, implicitThis, receiver, args);
    }

    protected static boolean isPrivateBridgeMethodsCallAllowed(ClassNode receiver, ClassNode caller) {
        if (receiver == null) {
            return false;
        }
        if (receiver.redirect() == caller) {
            return true;
        }
        if (caller.redirect() instanceof InnerClassNode) {
            return StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(receiver, caller.redirect().getOuterClass()) || StaticInvocationWriter.isPrivateBridgeMethodsCallAllowed(receiver.getOuterClass(), caller);
        }
        return false;
    }

    @Override
    protected void loadArguments(List<Expression> argumentList, Parameter[] para) {
        block13: {
            int argumentListSize;
            OperandStack operandStack;
            TypeChooser typeChooser;
            AsmClassGenerator acg;
            block12: {
                ClassNode lastArgType;
                if (para.length == 0) {
                    return;
                }
                ClassNode lastParaType = para[para.length - 1].getOriginType();
                acg = this.controller.getAcg();
                typeChooser = this.controller.getTypeChooser();
                operandStack = this.controller.getOperandStack();
                argumentListSize = argumentList.size();
                ClassNode classNode = lastArgType = argumentListSize > 0 ? typeChooser.resolveType(argumentList.get(argumentListSize - 1), this.controller.getClassNode()) : null;
                if (!lastParaType.isArray() || !(argumentListSize > para.length || argumentListSize == para.length - 1 && !lastParaType.equals(lastArgType) || argumentListSize == para.length && lastArgType != null && !lastArgType.isArray() && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(lastArgType, lastParaType.getComponentType())) && (!ClassHelper.GSTRING_TYPE.equals(lastArgType) || !ClassHelper.STRING_TYPE.equals(lastParaType.getComponentType()))) break block12;
                int stackLen = operandStack.getStackLength() + argumentListSize;
                MethodVisitor mv = this.controller.getMethodVisitor();
                this.controller.setMethodVisitor(mv);
                for (int i = 0; i < para.length - 1; ++i) {
                    Expression expression = argumentList.get(i);
                    expression.visit(acg);
                    if (StaticInvocationWriter.isNullConstant(expression)) continue;
                    operandStack.doGroovyCast(para[i].getType());
                }
                LinkedList<Expression> lastParams = new LinkedList<Expression>();
                for (int i = para.length - 1; i < argumentListSize; ++i) {
                    lastParams.add(argumentList.get(i));
                }
                ArrayExpression array = new ArrayExpression(lastParaType.getComponentType(), lastParams);
                array.visit(acg);
                while (operandStack.getStackLength() < stackLen) {
                    operandStack.push(ClassHelper.OBJECT_TYPE);
                }
                if (argumentListSize != para.length - 1) break block13;
                operandStack.remove(1);
                break block13;
            }
            if (argumentListSize == para.length) {
                for (int i = 0; i < argumentListSize; ++i) {
                    Expression expression = argumentList.get(i);
                    expression.visit(acg);
                    if (StaticInvocationWriter.isNullConstant(expression)) continue;
                    operandStack.doGroovyCast(para[i].getType());
                }
            } else {
                int i;
                ClassNode classNode = this.controller.getClassNode();
                Expression[] arguments = new Expression[para.length];
                int j = 0;
                for (i = 0; i < para.length; ++i) {
                    ClassNode curArgType;
                    Parameter curParam = para[i];
                    ClassNode curParamType = curParam.getType();
                    Expression curArg = j < argumentListSize ? argumentList.get(j) : null;
                    Expression initialExpression = (Expression)curParam.getNodeMetaData((Object)StaticTypesMarker.INITIAL_EXPRESSION);
                    if (initialExpression == null && curParam.hasInitialExpression()) {
                        initialExpression = curParam.getInitialExpression();
                    }
                    if (initialExpression == null && curParam.getNodeMetaData("INITIAL_EXPRESSION") != null) {
                        initialExpression = (Expression)curParam.getNodeMetaData("INITIAL_EXPRESSION");
                    }
                    ClassNode classNode2 = curArgType = curArg == null ? null : typeChooser.resolveType(curArg, classNode);
                    if (initialExpression != null && !this.compatibleArgumentType(curArgType, curParamType)) {
                        arguments[i] = initialExpression;
                        continue;
                    }
                    arguments[i] = curArg;
                    ++j;
                }
                for (i = 0; i < arguments.length; ++i) {
                    Expression expression = arguments[i];
                    expression.visit(acg);
                    if (StaticInvocationWriter.isNullConstant(expression)) continue;
                    operandStack.doGroovyCast(para[i].getType());
                }
            }
        }
    }

    private static boolean isNullConstant(Expression expression) {
        return expression instanceof ConstantExpression && ((ConstantExpression)expression).getValue() == null;
    }

    private boolean compatibleArgumentType(ClassNode argumentType, ClassNode paramType) {
        if (argumentType == null) {
            return false;
        }
        if (ClassHelper.getWrapper(argumentType).equals(ClassHelper.getWrapper(paramType))) {
            return true;
        }
        if (paramType.isInterface()) {
            return argumentType.implementsInterface(paramType);
        }
        if (paramType.isArray() && argumentType.isArray()) {
            return this.compatibleArgumentType(argumentType.getComponentType(), paramType.getComponentType());
        }
        return ClassHelper.getWrapper(argumentType).isDerivedFrom(ClassHelper.getWrapper(paramType));
    }

    @Override
    public void makeCall(Expression origin, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis) {
        ClassNode dynamicCallReturnType = (ClassNode)origin.getNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION);
        if (dynamicCallReturnType != null) {
            StaticTypesWriterController staticController = (StaticTypesWriterController)this.controller;
            if (origin instanceof MethodCallExpression) {
                ((MethodCallExpression)origin).setMethodTarget(null);
            }
            InvocationWriter dynamicInvocationWriter = staticController.getRegularInvocationWriter();
            dynamicInvocationWriter.makeCall(origin, receiver, message, arguments, adapter, safe, spreadSafe, implicitThis);
            return;
        }
        if (this.tryImplicitReceiver(origin, message, arguments, adapter, safe, spreadSafe, implicitThis)) {
            return;
        }
        if (spreadSafe && origin instanceof MethodCallExpression) {
            Expression tmpReceiver = receiver;
            if (!(receiver instanceof VariableExpression) && !(receiver instanceof ConstantExpression)) {
                tmpReceiver = new TemporaryVariableExpression(receiver);
            }
            MethodVisitor mv = this.controller.getMethodVisitor();
            CompileStack compileStack = this.controller.getCompileStack();
            TypeChooser typeChooser = this.controller.getTypeChooser();
            OperandStack operandStack = this.controller.getOperandStack();
            ClassNode classNode = this.controller.getClassNode();
            int counter = this.labelCounter.incrementAndGet();
            ConstructorCallExpression cce = new ConstructorCallExpression(StaticCompilationVisitor.ARRAYLIST_CLASSNODE, ArgumentListExpression.EMPTY_ARGUMENTS);
            cce.setNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, StaticCompilationVisitor.ARRAYLIST_CONSTRUCTOR);
            TemporaryVariableExpression result = new TemporaryVariableExpression(cce);
            result.visit(this.controller.getAcg());
            operandStack.pop();
            tmpReceiver.visit(this.controller.getAcg());
            Label ifnull = compileStack.createLocalLabel("ifnull_" + counter);
            mv.visitJumpInsn(198, ifnull);
            operandStack.remove(1);
            Label nonull = compileStack.createLocalLabel("nonull_" + counter);
            mv.visitLabel(nonull);
            ClassNode componentType = StaticTypeCheckingVisitor.inferLoopElementType(typeChooser.resolveType(tmpReceiver, classNode));
            Parameter iterator = new Parameter(componentType, "for$it$" + counter);
            VariableExpression iteratorAsVar = new VariableExpression(iterator);
            MethodCallExpression origMCE = (MethodCallExpression)origin;
            MethodCallExpression newMCE = new MethodCallExpression((Expression)iteratorAsVar, origMCE.getMethodAsString(), origMCE.getArguments());
            newMCE.setImplicitThis(false);
            newMCE.setMethodTarget(origMCE.getMethodTarget());
            newMCE.setSafe(true);
            MethodCallExpression add = new MethodCallExpression((Expression)result, "add", (Expression)newMCE);
            add.setImplicitThis(false);
            add.setMethodTarget(StaticCompilationVisitor.ARRAYLIST_ADD_METHOD);
            ForStatement stmt = new ForStatement(iterator, tmpReceiver, new ExpressionStatement(add));
            stmt.visit(this.controller.getAcg());
            mv.visitLabel(ifnull);
            result.visit(this.controller.getAcg());
            if (tmpReceiver instanceof TemporaryVariableExpression) {
                ((TemporaryVariableExpression)tmpReceiver).remove(this.controller);
            }
            result.remove(this.controller);
        } else if (safe && origin instanceof MethodCallExpression) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            CompileStack compileStack = this.controller.getCompileStack();
            OperandStack operandStack = this.controller.getOperandStack();
            int counter = this.labelCounter.incrementAndGet();
            ExpressionAsVariableSlot slot = new ExpressionAsVariableSlot(this.controller, receiver);
            slot.visit(this.controller.getAcg());
            operandStack.box();
            Label ifnull = compileStack.createLocalLabel("ifnull_" + counter);
            mv.visitJumpInsn(198, ifnull);
            operandStack.remove(1);
            Label nonull = compileStack.createLocalLabel("nonull_" + counter);
            mv.visitLabel(nonull);
            MethodCallExpression origMCE = (MethodCallExpression)origin;
            MethodCallExpression newMCE = new MethodCallExpression((Expression)new VariableSlotLoader(slot.getType(), slot.getIndex(), this.controller.getOperandStack()), origMCE.getMethodAsString(), origMCE.getArguments());
            MethodNode methodTarget = origMCE.getMethodTarget();
            newMCE.setMethodTarget(methodTarget);
            newMCE.setSafe(false);
            newMCE.setImplicitThis(origMCE.isImplicitThis());
            newMCE.setSourcePosition(origMCE);
            newMCE.visit(this.controller.getAcg());
            compileStack.removeVar(slot.getIndex());
            ClassNode returnType = operandStack.getTopOperand();
            if (ClassHelper.isPrimitiveType(returnType) && !ClassHelper.VOID_TYPE.equals(returnType)) {
                operandStack.box();
            }
            Label endof = compileStack.createLocalLabel("endof_" + counter);
            mv.visitJumpInsn(167, endof);
            mv.visitLabel(ifnull);
            mv.visitInsn(1);
            mv.visitLabel(endof);
        } else {
            if ((adapter == AsmClassGenerator.getGroovyObjectField || adapter == AsmClassGenerator.getField) && origin instanceof AttributeExpression) {
                TypeChooser typeChooser;
                StaticTypesCallSiteWriter stcsw;
                String pname = ((PropertyExpression)origin).getPropertyAsString();
                CallSiteWriter callSiteWriter = this.controller.getCallSiteWriter();
                if (pname != null && callSiteWriter instanceof StaticTypesCallSiteWriter && (stcsw = (StaticTypesCallSiteWriter)callSiteWriter).makeGetField(receiver, (typeChooser = this.controller.getTypeChooser()).resolveType(receiver, this.controller.getClassNode()), pname, safe, false, true)) {
                    return;
                }
            }
            super.makeCall(origin, receiver, message, arguments, adapter, safe, spreadSafe, implicitThis);
        }
    }

    boolean tryImplicitReceiver(Expression origin, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis) {
        Object implicitReceiver = origin.getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER);
        if (implicitThis && implicitReceiver == null && origin instanceof MethodCallExpression) {
            implicitReceiver = ((MethodCallExpression)origin).getObjectExpression().getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER);
        }
        if (implicitReceiver != null && implicitThis) {
            String[] propertyPath = ((String)implicitReceiver).split("\\.");
            PropertyExpression pexp = new PropertyExpression((Expression)new VariableExpression("this", ClassHelper.CLOSURE_TYPE), propertyPath[0]);
            pexp.setImplicitThis(true);
            for (int i = 1; i < propertyPath.length; ++i) {
                pexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, ClassHelper.CLOSURE_TYPE);
                pexp = new PropertyExpression((Expression)pexp, propertyPath[i]);
            }
            pexp.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, implicitReceiver);
            origin.removeNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER);
            if (origin instanceof PropertyExpression) {
                PropertyExpression rewritten = new PropertyExpression(pexp, ((PropertyExpression)origin).getProperty(), ((PropertyExpression)origin).isSafe());
                rewritten.setSpreadSafe(((PropertyExpression)origin).isSpreadSafe());
                rewritten.setImplicitThis(false);
                rewritten.visit(this.controller.getAcg());
                rewritten.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, origin.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE));
                return true;
            }
            this.makeCall(origin, pexp, message, arguments, adapter, safe, spreadSafe, false);
            return true;
        }
        return false;
    }

    public MethodCallExpression getCurrentCall() {
        return this.currentCall;
    }

    @Override
    protected boolean makeCachedCall(Expression origin, ClassExpression sender, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis, boolean containsSpreadExpression) {
        return false;
    }

    private class CheckcastReceiverExpression
    extends Expression {
        private final Expression receiver;
        private final MethodNode target;
        private ClassNode resolvedType;

        public CheckcastReceiverExpression(Expression receiver, MethodNode target) {
            this.receiver = receiver;
            this.target = target;
        }

        @Override
        public Expression transformExpression(ExpressionTransformer transformer) {
            return this;
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            this.receiver.visit(visitor);
            if (visitor instanceof AsmClassGenerator) {
                ClassNode topOperand = StaticInvocationWriter.this.controller.getOperandStack().getTopOperand();
                ClassNode type = this.getType();
                if (ClassHelper.GSTRING_TYPE.equals(topOperand) && ClassHelper.STRING_TYPE.equals(type)) {
                    StaticInvocationWriter.this.controller.getOperandStack().doGroovyCast(type);
                    return;
                }
                if (ClassHelper.isPrimitiveType(topOperand) && !ClassHelper.isPrimitiveType(type)) {
                    StaticInvocationWriter.this.controller.getOperandStack().box();
                } else if (!ClassHelper.isPrimitiveType(topOperand) && ClassHelper.isPrimitiveType(type)) {
                    StaticInvocationWriter.this.controller.getOperandStack().doGroovyCast(type);
                }
                if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(topOperand, type)) {
                    return;
                }
                StaticInvocationWriter.this.controller.getMethodVisitor().visitTypeInsn(192, type.isArray() ? BytecodeHelper.getTypeDescription(type) : BytecodeHelper.getClassInternalName(type.getName()));
                StaticInvocationWriter.this.controller.getOperandStack().replace(type);
            }
        }

        @Override
        public ClassNode getType() {
            ClassNode type;
            if (this.resolvedType != null) {
                return this.resolvedType;
            }
            if (this.target instanceof ExtensionMethodNode) {
                type = ((ExtensionMethodNode)this.target).getExtensionMethodNode().getDeclaringClass();
            } else {
                type = ClassHelper.getWrapper(StaticInvocationWriter.this.controller.getTypeChooser().resolveType(this.receiver, StaticInvocationWriter.this.controller.getClassNode()));
                ClassNode declaringClass = this.target.getDeclaringClass();
                if (type.getClass() != ClassNode.class && type.getClass() != InnerClassNode.class) {
                    type = declaringClass;
                }
                if (ClassHelper.OBJECT_TYPE.equals(type) && !ClassHelper.OBJECT_TYPE.equals(declaringClass)) {
                    type = declaringClass;
                }
                if (ClassHelper.OBJECT_TYPE.equals(declaringClass)) {
                    type = ClassHelper.OBJECT_TYPE;
                }
            }
            this.resolvedType = type;
            return type;
        }
    }
}

