/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.MethodCallerMultiAdapter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.OptimizingStatementWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.syntax.SyntaxException;

public class InvocationWriter {
    public static final MethodCallerMultiAdapter invokeMethodOnCurrent = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "invokeMethodOnCurrent", true, false);
    public static final MethodCallerMultiAdapter invokeMethodOnSuper = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "invokeMethodOnSuper", true, false);
    public static final MethodCallerMultiAdapter invokeMethod = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "invokeMethod", true, false);
    public static final MethodCallerMultiAdapter invokeStaticMethod = MethodCallerMultiAdapter.newStatic(ScriptBytecodeAdapter.class, "invokeStaticMethod", true, true);
    public static final MethodCaller invokeClosureMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "invokeClosure");
    public static final MethodCaller castToVargsArray = MethodCaller.newStatic(DefaultTypeTransformation.class, "castToVargsArray");
    private static final MethodNode CLASS_FOR_NAME_STRING = ClassHelper.CLASS_Type.getDeclaredMethod("forName", new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name")});
    private static final MethodCaller asTypeMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "asType");
    private static final MethodCaller castToTypeMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "castToType");
    private static final MethodCaller castToClassMethod = MethodCaller.newStatic(ShortTypeHandling.class, "castToClass");
    private static final MethodCaller castToStringMethod = MethodCaller.newStatic(ShortTypeHandling.class, "castToString");
    private static final MethodCaller castToEnumMethod = MethodCaller.newStatic(ShortTypeHandling.class, "castToEnum");
    static final MethodCaller selectConstructorAndTransformArguments = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "selectConstructorAndTransformArguments");
    private WriterController controller;

    public InvocationWriter(WriterController wc) {
        this.controller = wc;
    }

    private void makeInvokeMethodCall(MethodCallExpression call, boolean useSuper, MethodCallerMultiAdapter adapter) {
        Expression objectExpression = call.getObjectExpression();
        CastExpression messageName = new CastExpression(ClassHelper.STRING_TYPE, call.getMethod());
        if (useSuper) {
            ClassNode classNode = this.controller.isInClosure() ? this.controller.getOutermostClass() : this.controller.getClassNode();
            ClassNode superClass = classNode.getSuperClass();
            this.makeCall(call, new ClassExpression(superClass), objectExpression, messageName, call.getArguments(), adapter, call.isSafe(), call.isSpreadSafe(), false);
        } else {
            this.makeCall(call, objectExpression, messageName, call.getArguments(), adapter, call.isSafe(), call.isSpreadSafe(), call.isImplicitThis());
        }
    }

    public void makeCall(Expression origin, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis) {
        ClassNode cn = this.controller.getClassNode();
        if (this.controller.isInClosure() && !implicitThis && AsmClassGenerator.isThisExpression(receiver)) {
            cn = cn.getOuterClass();
        }
        this.makeCall(origin, new ClassExpression(cn), receiver, message, arguments, adapter, safe, spreadSafe, implicitThis);
    }

    protected boolean writeDirectMethodCall(MethodNode target, boolean implicitThis, Expression receiver, TupleExpression args) {
        ClassNode receiverType;
        if (target == null) {
            return false;
        }
        String methodName = target.getName();
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        ClassNode declaringClass = target.getDeclaringClass();
        ClassNode classNode = this.controller.getClassNode();
        MethodVisitor mv = this.controller.getMethodVisitor();
        int opcode = 182;
        if (target.isStatic()) {
            opcode = 184;
        } else if (target.isPrivate() || receiver instanceof VariableExpression && ((VariableExpression)receiver).isSuperExpression()) {
            opcode = 183;
        } else if (declaringClass.isInterface()) {
            opcode = 185;
        }
        int argumentsToRemove = 0;
        if (opcode != 184) {
            if (receiver != null) {
                if (implicitThis && !classNode.isDerivedFrom(declaringClass) && !classNode.implementsInterface(declaringClass) && classNode instanceof InnerClassNode) {
                    compileStack.pushImplicitThis(false);
                    if (this.controller.isInClosure()) {
                        new VariableExpression("thisObject").visit(this.controller.getAcg());
                    } else {
                        PropertyExpression expr = new PropertyExpression((Expression)new ClassExpression(declaringClass), "this");
                        ((ASTNode)expr).visit(this.controller.getAcg());
                    }
                } else {
                    compileStack.pushImplicitThis(implicitThis);
                    receiver.visit(this.controller.getAcg());
                }
                operandStack.doGroovyCast(declaringClass);
                compileStack.popImplicitThis();
                ++argumentsToRemove;
            } else {
                mv.visitIntInsn(25, 0);
                operandStack.push(classNode);
                ++argumentsToRemove;
            }
        }
        int stackSize = operandStack.getStackLength();
        String owner = BytecodeHelper.getClassInternalName(declaringClass);
        ClassNode classNode2 = receiverType = receiver != null ? this.controller.getTypeChooser().resolveType(receiver, classNode) : declaringClass;
        if (opcode == 182 && ClassHelper.OBJECT_TYPE.equals(declaringClass)) {
            receiverType = declaringClass;
        }
        if (opcode == 182) {
            if (!(receiverType.equals(declaringClass) || ClassHelper.OBJECT_TYPE.equals(declaringClass) || receiverType.isArray() || receiverType.isInterface() || ClassHelper.isPrimitiveType(receiverType) || !receiverType.isDerivedFrom(declaringClass))) {
                owner = BytecodeHelper.getClassInternalName(receiverType);
                ClassNode top = operandStack.getTopOperand();
                if (!receiverType.equals(top)) {
                    mv.visitTypeInsn(192, owner);
                }
            } else if (target.isPublic() && !Modifier.isPublic(declaringClass.getModifiers()) && !receiverType.equals(declaringClass) && receiverType.isDerivedFrom(declaringClass) && !receiverType.getPackageName().equals(classNode.getPackageName())) {
                owner = BytecodeHelper.getClassInternalName(receiverType);
            }
        }
        this.loadArguments(args.getExpressions(), target.getParameters());
        String desc = BytecodeHelper.getMethodDescriptor(target.getReturnType(), target.getParameters());
        mv.visitMethodInsn(opcode, owner, methodName, desc, opcode == 185);
        ClassNode ret = target.getReturnType().redirect();
        if (ret == ClassHelper.VOID_TYPE) {
            ret = ClassHelper.OBJECT_TYPE;
            mv.visitInsn(1);
        }
        this.controller.getOperandStack().remove(argumentsToRemove += operandStack.getStackLength() - stackSize);
        this.controller.getOperandStack().push(ret);
        return true;
    }

    private boolean lastIsArray(List<Expression> argumentList, int pos) {
        Expression last = argumentList.get(pos);
        ClassNode type = this.controller.getTypeChooser().resolveType(last, this.controller.getClassNode());
        return type.isArray();
    }

    protected void loadArguments(List<Expression> argumentList, Parameter[] para) {
        if (para.length == 0) {
            return;
        }
        ClassNode lastParaType = para[para.length - 1].getOriginType();
        AsmClassGenerator acg = this.controller.getAcg();
        OperandStack operandStack = this.controller.getOperandStack();
        if (lastParaType.isArray() && (argumentList.size() > para.length || argumentList.size() == para.length - 1 || !this.lastIsArray(argumentList, para.length - 1))) {
            int stackLen = operandStack.getStackLength() + argumentList.size();
            MethodVisitor mv = this.controller.getMethodVisitor();
            this.controller.setMethodVisitor(mv);
            for (int i = 0; i < para.length - 1; ++i) {
                argumentList.get(i).visit(acg);
                operandStack.doGroovyCast(para[i].getType());
            }
            LinkedList<Expression> lastParams = new LinkedList<Expression>();
            for (int i = para.length - 1; i < argumentList.size(); ++i) {
                lastParams.add(argumentList.get(i));
            }
            ArrayExpression array = new ArrayExpression(lastParaType.getComponentType(), lastParams);
            array.visit(acg);
            while (operandStack.getStackLength() < stackLen) {
                operandStack.push(ClassHelper.OBJECT_TYPE);
            }
            if (argumentList.size() == para.length - 1) {
                operandStack.remove(1);
            }
        } else {
            for (int i = 0; i < argumentList.size(); ++i) {
                argumentList.get(i).visit(acg);
                operandStack.doGroovyCast(para[i].getType());
            }
        }
    }

    protected boolean makeDirectCall(Expression origin, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean implicitThis, boolean containsSpreadExpression) {
        String methodName;
        boolean fittingAdapter;
        if (this.makeClassForNameCall(origin, receiver, message, arguments)) {
            return true;
        }
        boolean bl = fittingAdapter = adapter == invokeMethodOnCurrent || adapter == invokeStaticMethod;
        if (fittingAdapter && this.controller.optimizeForInt && this.controller.isFastPath() && (methodName = this.getMethodName(message)) != null) {
            TupleExpression args = arguments instanceof TupleExpression ? (TupleExpression)arguments : new TupleExpression(receiver);
            OptimizingStatementWriter.StatementMeta meta = null;
            if (origin != null) {
                meta = (OptimizingStatementWriter.StatementMeta)origin.getNodeMetaData(OptimizingStatementWriter.StatementMeta.class);
            }
            MethodNode mn = null;
            if (meta != null) {
                mn = meta.target;
            }
            if (this.writeDirectMethodCall(mn, true, null, args)) {
                return true;
            }
        }
        if (containsSpreadExpression) {
            return false;
        }
        if (origin instanceof MethodCallExpression) {
            MethodCallExpression mce = (MethodCallExpression)origin;
            MethodNode target = mce.getMethodTarget();
            return this.writeDirectMethodCall(target, implicitThis, receiver, InvocationWriter.makeArgumentList(arguments));
        }
        return false;
    }

    protected boolean makeCachedCall(Expression origin, ClassExpression sender, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis, boolean containsSpreadExpression) {
        String methodName;
        if (!(adapter != invokeMethod && adapter != invokeMethodOnCurrent && adapter != invokeStaticMethod || spreadSafe || (methodName = this.getMethodName(message)) == null)) {
            this.controller.getCallSiteWriter().makeCallSite(receiver, methodName, arguments, safe, implicitThis, adapter == invokeMethodOnCurrent, adapter == invokeStaticMethod);
            return true;
        }
        return false;
    }

    protected void makeUncachedCall(Expression origin, ClassExpression sender, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis, boolean containsSpreadExpression) {
        int numberOfArguments;
        OperandStack operandStack = this.controller.getOperandStack();
        CompileStack compileStack = this.controller.getCompileStack();
        AsmClassGenerator acg = this.controller.getAcg();
        compileStack.pushLHS(false);
        if (adapter == AsmClassGenerator.setProperty) {
            ConstantExpression.NULL.visit(acg);
        } else {
            sender.visit(acg);
        }
        String methodName = this.getMethodName(message);
        if (adapter == invokeMethodOnSuper && methodName != null) {
            this.controller.getSuperMethodNames().add(methodName);
        }
        compileStack.pushImplicitThis(implicitThis);
        receiver.visit(acg);
        operandStack.box();
        compileStack.popImplicitThis();
        int operandsToRemove = 2;
        if (message != null) {
            message.visit(acg);
            operandStack.box();
            ++operandsToRemove;
        }
        int n = numberOfArguments = containsSpreadExpression ? -1 : AsmClassGenerator.argumentSize(arguments);
        if (numberOfArguments > 0 || containsSpreadExpression) {
            ArgumentListExpression ae = InvocationWriter.makeArgumentList(arguments);
            if (containsSpreadExpression) {
                acg.despreadList(ae.getExpressions(), true);
            } else {
                ae.visit(acg);
            }
        } else if (numberOfArguments > 0) {
            operandsToRemove += numberOfArguments;
            TupleExpression te = (TupleExpression)arguments;
            for (int i = 0; i < numberOfArguments; ++i) {
                Expression argument = te.getExpression(i);
                argument.visit(acg);
                operandStack.box();
                if (!(argument instanceof CastExpression)) continue;
                acg.loadWrapper(argument);
            }
        }
        if (adapter == null) {
            adapter = invokeMethod;
        }
        adapter.call(this.controller.getMethodVisitor(), numberOfArguments, safe, spreadSafe);
        compileStack.popLHS();
        operandStack.replace(ClassHelper.OBJECT_TYPE, operandsToRemove);
    }

    protected void makeCall(Expression origin, ClassExpression sender, Expression receiver, Expression message, Expression arguments, MethodCallerMultiAdapter adapter, boolean safe, boolean spreadSafe, boolean implicitThis) {
        boolean containsSpreadExpression = AsmClassGenerator.containsSpreadExpression(arguments);
        if (this.makeDirectCall(origin, receiver, message, arguments, adapter, implicitThis, containsSpreadExpression)) {
            return;
        }
        if (this.makeCachedCall(origin, sender, receiver, message, arguments, adapter, safe, spreadSafe, implicitThis, containsSpreadExpression)) {
            return;
        }
        this.makeUncachedCall(origin, sender, receiver, message, arguments, adapter, safe, spreadSafe, implicitThis, containsSpreadExpression);
    }

    protected boolean makeClassForNameCall(Expression origin, Expression receiver, Expression message, Expression arguments) {
        if (!(receiver instanceof ClassExpression)) {
            return false;
        }
        ClassExpression ce = (ClassExpression)receiver;
        if (!ClassHelper.CLASS_Type.equals(ce.getType())) {
            return false;
        }
        String msg = this.getMethodName(message);
        if (!"forName".equals(msg)) {
            return false;
        }
        ArgumentListExpression ae = InvocationWriter.makeArgumentList(arguments);
        if (ae.getExpressions().size() != 1) {
            return false;
        }
        return this.writeDirectMethodCall(CLASS_FOR_NAME_STRING, false, receiver, ae);
    }

    public static ArgumentListExpression makeArgumentList(Expression arguments) {
        ArgumentListExpression ae;
        if (arguments instanceof ArgumentListExpression) {
            ae = (ArgumentListExpression)arguments;
        } else if (arguments instanceof TupleExpression) {
            TupleExpression te = (TupleExpression)arguments;
            ae = new ArgumentListExpression(te.getExpressions());
        } else {
            ae = new ArgumentListExpression();
            ae.addExpression(arguments);
        }
        return ae;
    }

    protected String getMethodName(Expression message) {
        Expression methodExpr;
        CastExpression msg;
        String methodName = null;
        if (message instanceof CastExpression && (msg = (CastExpression)message).getType() == ClassHelper.STRING_TYPE && (methodExpr = msg.getExpression()) instanceof ConstantExpression) {
            methodName = methodExpr.getText();
        }
        if (methodName == null && message instanceof ConstantExpression) {
            ConstantExpression constantExpression = (ConstantExpression)message;
            methodName = constantExpression.getText();
        }
        return methodName;
    }

    public void writeInvokeMethod(MethodCallExpression call) {
        if (this.isClosureCall(call)) {
            this.invokeClosure(call.getArguments(), call.getMethodAsString());
        } else {
            boolean isSuperMethodCall = InvocationWriter.usesSuper(call);
            MethodCallerMultiAdapter adapter = invokeMethod;
            if (isSuperMethodCall && call.isSafe()) {
                call.setSafe(false);
            }
            if (AsmClassGenerator.isThisExpression(call.getObjectExpression())) {
                adapter = invokeMethodOnCurrent;
            }
            if (isSuperMethodCall) {
                adapter = invokeMethodOnSuper;
            }
            if (this.isStaticInvocation(call)) {
                adapter = invokeStaticMethod;
            }
            this.makeInvokeMethodCall(call, isSuperMethodCall, adapter);
        }
    }

    private boolean isClosureCall(MethodCallExpression call) {
        ClassNode classNode = this.controller.getClassNode();
        String methodName = call.getMethodAsString();
        if (methodName == null) {
            return false;
        }
        if (!call.isImplicitThis()) {
            return false;
        }
        if (!AsmClassGenerator.isThisExpression(call.getObjectExpression())) {
            return false;
        }
        FieldNode field = classNode.getDeclaredField(methodName);
        if (field == null) {
            return false;
        }
        if (this.isStaticInvocation(call) && !field.isStatic()) {
            return false;
        }
        Expression arguments = call.getArguments();
        return !classNode.hasPossibleMethod(methodName, arguments);
    }

    private void invokeClosure(Expression arguments, String methodName) {
        AsmClassGenerator acg = this.controller.getAcg();
        acg.visitVariableExpression(new VariableExpression(methodName));
        this.controller.getOperandStack().box();
        if (arguments instanceof TupleExpression) {
            arguments.visit(acg);
        } else {
            new TupleExpression(arguments).visit(acg);
        }
        invokeClosureMethod.call(this.controller.getMethodVisitor());
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
    }

    private boolean isStaticInvocation(MethodCallExpression call) {
        if (!AsmClassGenerator.isThisExpression(call.getObjectExpression())) {
            return false;
        }
        if (this.controller.isStaticMethod()) {
            return true;
        }
        return this.controller.isStaticContext() && !call.isImplicitThis();
    }

    private static boolean usesSuper(MethodCallExpression call) {
        Expression expression = call.getObjectExpression();
        if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression)expression;
            String variable = varExp.getName();
            return variable.equals("super");
        }
        return false;
    }

    public void writeInvokeStaticMethod(StaticMethodCallExpression call) {
        this.makeCall(call, new ClassExpression(call.getOwnerType()), new ConstantExpression(call.getMethod()), call.getArguments(), invokeStaticMethod, false, false, false);
    }

    private boolean writeDirectConstructorCall(ConstructorCallExpression call) {
        if (!this.controller.isFastPath()) {
            return false;
        }
        OptimizingStatementWriter.StatementMeta meta = (OptimizingStatementWriter.StatementMeta)call.getNodeMetaData(OptimizingStatementWriter.StatementMeta.class);
        ConstructorNode cn = null;
        if (meta != null) {
            cn = (ConstructorNode)meta.target;
        }
        if (cn == null) {
            return false;
        }
        String ownerDescriptor = this.prepareConstructorCall(cn);
        ArgumentListExpression args = InvocationWriter.makeArgumentList(call.getArguments());
        this.loadArguments(args.getExpressions(), cn.getParameters());
        this.finnishConstructorCall(cn, ownerDescriptor, args.getExpressions().size());
        return true;
    }

    protected String prepareConstructorCall(ConstructorNode cn) {
        String owner = BytecodeHelper.getClassInternalName(cn.getDeclaringClass());
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitTypeInsn(187, owner);
        mv.visitInsn(89);
        return owner;
    }

    protected void finnishConstructorCall(ConstructorNode cn, String ownerDescriptor, int argsToRemove) {
        String desc = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, cn.getParameters());
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitMethodInsn(183, ownerDescriptor, "<init>", desc, false);
        this.controller.getOperandStack().remove(argsToRemove);
        this.controller.getOperandStack().push(cn.getDeclaringClass());
    }

    protected void writeNormalConstructorCall(ConstructorCallExpression call) {
        TupleExpression tupleExpression;
        int size;
        Expression arguments = call.getArguments();
        if (arguments instanceof TupleExpression && (size = (tupleExpression = (TupleExpression)arguments).getExpressions().size()) == 0) {
            arguments = MethodCallExpression.NO_ARGUMENTS;
        }
        ClassExpression receiverClass = new ClassExpression(call.getType());
        this.controller.getCallSiteWriter().makeCallSite(receiverClass, "<$constructor$>", arguments, false, false, false, false);
    }

    public void writeInvokeConstructor(ConstructorCallExpression call) {
        if (this.writeDirectConstructorCall(call)) {
            return;
        }
        if (this.writeAICCall(call)) {
            return;
        }
        this.writeNormalConstructorCall(call);
    }

    protected boolean writeAICCall(ConstructorCallExpression call) {
        if (!call.isUsingAnonymousInnerClass()) {
            return false;
        }
        ConstructorNode cn = call.getType().getDeclaredConstructors().get(0);
        OperandStack os = this.controller.getOperandStack();
        String ownerDescriptor = this.prepareConstructorCall(cn);
        List<Expression> args = InvocationWriter.makeArgumentList(call.getArguments()).getExpressions();
        Parameter[] params = cn.getParameters();
        this.controller.getCompileStack().pushImplicitThis(true);
        for (int i = 0; i < params.length; ++i) {
            Parameter p = params[i];
            Expression arg = args.get(i);
            if (arg instanceof VariableExpression) {
                VariableExpression var = (VariableExpression)arg;
                this.loadVariableWithReference(var);
            } else {
                arg.visit(this.controller.getAcg());
            }
            os.doGroovyCast(p.getType());
        }
        this.controller.getCompileStack().popImplicitThis();
        this.finnishConstructorCall(cn, ownerDescriptor, args.size());
        return true;
    }

    private void loadVariableWithReference(VariableExpression var) {
        if (!var.isUseReferenceDirectly()) {
            var.visit(this.controller.getAcg());
        } else {
            ClosureWriter.loadReference(var.getName(), this.controller);
        }
    }

    public void makeSingleArgumentCall(Expression receiver, String message, Expression arguments) {
        this.controller.getCallSiteWriter().makeSingleArgumentCall(receiver, message, arguments);
    }

    public void writeSpecialConstructorCall(ConstructorCallExpression call) {
        this.controller.getCompileStack().pushInSpecialConstructorCall();
        this.visitSpecialConstructorCall(call);
        this.controller.getCompileStack().pop();
    }

    private void visitSpecialConstructorCall(ConstructorCallExpression call) {
        List<ConstructorNode> constructors;
        if (this.controller.getClosureWriter().addGeneratedClosureConstructorCall(call)) {
            return;
        }
        ClassNode callNode = this.controller.getClassNode();
        if (call.isSuperCall()) {
            callNode = callNode.getSuperClass();
        }
        if (!this.makeDirectConstructorCall(constructors = InvocationWriter.sortConstructors(call, callNode), call, callNode)) {
            this.makeMOPBasedConstructorCall(constructors, call, callNode);
        }
    }

    private static List<ConstructorNode> sortConstructors(ConstructorCallExpression call, ClassNode callNode) {
        ArrayList<ConstructorNode> constructors = new ArrayList<ConstructorNode>(callNode.getDeclaredConstructors());
        Comparator comp = new Comparator(){

            public int compare(Object arg0, Object arg1) {
                ConstructorNode c0 = (ConstructorNode)arg0;
                ConstructorNode c1 = (ConstructorNode)arg1;
                String descriptor0 = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, c0.getParameters());
                String descriptor1 = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, c1.getParameters());
                return descriptor0.compareTo(descriptor1);
            }
        };
        Collections.sort(constructors, comp);
        return constructors;
    }

    private boolean makeDirectConstructorCall(List<ConstructorNode> constructors, ConstructorCallExpression call, ClassNode callNode) {
        List<Expression> argumentList;
        if (!this.controller.isConstructor()) {
            return false;
        }
        Expression arguments = call.getArguments();
        if (arguments instanceof TupleExpression) {
            argumentList = ((TupleExpression)arguments).getExpressions();
        } else {
            argumentList = new ArrayList<Expression>();
            argumentList.add(arguments);
        }
        for (Expression expression : argumentList) {
            if (!(expression instanceof SpreadExpression)) continue;
            return false;
        }
        ConstructorNode cn = InvocationWriter.getMatchingConstructor(constructors, argumentList);
        if (cn == null) {
            return false;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        Parameter[] params = cn.getParameters();
        mv.visitVarInsn(25, 0);
        for (int i = 0; i < params.length; ++i) {
            Expression expression = argumentList.get(i);
            expression.visit(this.controller.getAcg());
            if (!AsmClassGenerator.isNullConstant(expression)) {
                operandStack.doGroovyCast(params[i].getType());
            }
            operandStack.remove(1);
        }
        String descriptor = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, params);
        mv.visitMethodInsn(183, BytecodeHelper.getClassInternalName(callNode), "<init>", descriptor, false);
        return true;
    }

    private void makeMOPBasedConstructorCall(List<ConstructorNode> constructors, ConstructorCallExpression call, ClassNode callNode) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        call.getArguments().visit(this.controller.getAcg());
        mv.visitInsn(89);
        BytecodeHelper.pushConstant(mv, -1);
        this.controller.getAcg().visitClassExpression(new ClassExpression(callNode));
        operandStack.remove(1);
        selectConstructorAndTransformArguments.call(mv);
        if (this.controller.isConstructor()) {
            mv.visitVarInsn(25, 0);
        } else {
            mv.visitTypeInsn(187, BytecodeHelper.getClassInternalName(callNode));
        }
        mv.visitInsn(95);
        TreeMap<Integer, ConstructorNode> sortedConstructors = new TreeMap<Integer, ConstructorNode>();
        for (ConstructorNode constructor : constructors) {
            String typeDescriptor = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, constructor.getParameters());
            int hash = BytecodeHelper.hashCode(typeDescriptor);
            ConstructorNode sameHashNode = sortedConstructors.put(hash, constructor);
            if (sameHashNode == null) continue;
            this.controller.getSourceUnit().addError(new SyntaxException("Unable to compile class " + this.controller.getClassNode().getName() + " due to hash collision in constructors", call.getLineNumber(), call.getColumnNumber()));
        }
        Label[] targets = new Label[constructors.size()];
        int[] indices = new int[constructors.size()];
        Iterator hashIt = sortedConstructors.keySet().iterator();
        Iterator constructorIt = sortedConstructors.values().iterator();
        for (int i = 0; i < targets.length; ++i) {
            targets[i] = new Label();
            indices[i] = (Integer)hashIt.next();
        }
        Label defaultLabel = new Label();
        Label afterSwitch = new Label();
        mv.visitLookupSwitchInsn(defaultLabel, indices, targets);
        for (int i = 0; i < targets.length; ++i) {
            mv.visitLabel(targets[i]);
            if (this.controller.isConstructor()) {
                mv.visitInsn(95);
                mv.visitInsn(90);
            } else {
                mv.visitInsn(90);
                mv.visitInsn(93);
                mv.visitInsn(87);
            }
            ConstructorNode cn = (ConstructorNode)constructorIt.next();
            String descriptor = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, cn.getParameters());
            Parameter[] parameters = cn.getParameters();
            int lengthWithoutVargs = parameters.length;
            if (parameters.length > 0 && parameters[parameters.length - 1].getType().isArray()) {
                --lengthWithoutVargs;
            }
            for (int p = 0; p < lengthWithoutVargs; ++p) {
                InvocationWriter.loadAndCastElement(operandStack, mv, parameters, p);
            }
            if (parameters.length > lengthWithoutVargs) {
                ClassNode type = parameters[lengthWithoutVargs].getType();
                BytecodeHelper.pushConstant(mv, lengthWithoutVargs);
                this.controller.getAcg().visitClassExpression(new ClassExpression(type));
                operandStack.remove(1);
                castToVargsArray.call(mv);
                BytecodeHelper.doCast(mv, type);
            } else {
                mv.visitInsn(87);
            }
            mv.visitMethodInsn(183, BytecodeHelper.getClassInternalName(callNode), "<init>", descriptor, false);
            mv.visitJumpInsn(167, afterSwitch);
        }
        mv.visitLabel(defaultLabel);
        mv.visitTypeInsn(187, "java/lang/IllegalArgumentException");
        mv.visitInsn(89);
        mv.visitLdcInsn("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
        mv.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
        mv.visitLabel(afterSwitch);
        if (!this.controller.isConstructor()) {
            mv.visitInsn(95);
            operandStack.push(callNode);
        }
        mv.visitInsn(87);
    }

    private static void loadAndCastElement(OperandStack operandStack, MethodVisitor mv, Parameter[] parameters, int p) {
        operandStack.push(ClassHelper.OBJECT_TYPE);
        mv.visitInsn(89);
        BytecodeHelper.pushConstant(mv, p);
        mv.visitInsn(50);
        operandStack.push(ClassHelper.OBJECT_TYPE);
        ClassNode type = parameters[p].getType();
        operandStack.doGroovyCast(type);
        operandStack.swap();
        operandStack.remove(2);
    }

    private static ConstructorNode getMatchingConstructor(List<ConstructorNode> constructors, List<Expression> argumentList) {
        ConstructorNode lastMatch = null;
        for (int i = 0; i < constructors.size(); ++i) {
            ConstructorNode cn = constructors.get(i);
            Parameter[] params = cn.getParameters();
            if (argumentList.size() != params.length) continue;
            if (lastMatch == null) {
                lastMatch = cn;
                continue;
            }
            return null;
        }
        return lastMatch;
    }

    public void castToNonPrimitiveIfNecessary(ClassNode sourceType, ClassNode targetType) {
        OperandStack os = this.controller.getOperandStack();
        ClassNode boxedType = os.box();
        if (WideningCategories.implementsInterfaceOrSubclassOf(boxedType, targetType)) {
            return;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (ClassHelper.CLASS_Type.equals(targetType)) {
            castToClassMethod.call(mv);
        } else if (ClassHelper.STRING_TYPE.equals(targetType)) {
            castToStringMethod.call(mv);
        } else if (targetType.isDerivedFrom(ClassHelper.Enum_Type)) {
            new ClassExpression(targetType).visit(this.controller.getAcg());
            os.remove(1);
            castToEnumMethod.call(mv);
            BytecodeHelper.doCast(mv, targetType);
        } else {
            new ClassExpression(targetType).visit(this.controller.getAcg());
            os.remove(1);
            castToTypeMethod.call(mv);
        }
    }

    public void castNonPrimitiveToBool(ClassNode last) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        BytecodeHelper.unbox(mv, ClassHelper.boolean_TYPE);
    }

    public void coerce(ClassNode from, ClassNode target) {
        if (from.isDerivedFrom(target)) {
            return;
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack os = this.controller.getOperandStack();
        os.box();
        new ClassExpression(target).visit(this.controller.getAcg());
        os.remove(1);
        asTypeMethod.call(mv);
        BytecodeHelper.doCast(mv, target);
        os.replace(target);
    }
}

