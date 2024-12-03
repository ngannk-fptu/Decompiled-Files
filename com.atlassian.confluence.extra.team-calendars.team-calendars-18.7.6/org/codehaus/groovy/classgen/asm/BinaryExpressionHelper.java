/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovy.lang.GroovyRuntimeException;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.BinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.ExpressionAsVariableSlot;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.VariableSlotLoader;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;

public class BinaryExpressionHelper {
    private static final MethodCaller compareEqualMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareEqual");
    private static final MethodCaller compareNotEqualMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareNotEqual");
    private static final MethodCaller compareToMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareTo");
    private static final MethodCaller compareLessThanMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareLessThan");
    private static final MethodCaller compareLessThanEqualMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareLessThanEqual");
    private static final MethodCaller compareGreaterThanMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareGreaterThan");
    private static final MethodCaller compareGreaterThanEqualMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "compareGreaterThanEqual");
    private static final MethodCaller findRegexMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "findRegex");
    private static final MethodCaller matchRegexMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "matchRegex");
    private static final MethodCaller isCaseMethod = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "isCase");
    private WriterController controller;

    public BinaryExpressionHelper(WriterController wc) {
        this.controller = wc;
    }

    public WriterController getController() {
        return this.controller;
    }

    public void eval(BinaryExpression expression) {
        switch (expression.getOperation().getType()) {
            case 100: {
                this.evaluateEqual(expression, false);
                break;
            }
            case 123: {
                this.evaluateCompareExpression(compareEqualMethod, expression);
                break;
            }
            case 120: {
                this.evaluateCompareExpression(compareNotEqualMethod, expression);
                break;
            }
            case 128: {
                this.evaluateCompareTo(expression);
                break;
            }
            case 126: {
                this.evaluateCompareExpression(compareGreaterThanMethod, expression);
                break;
            }
            case 127: {
                this.evaluateCompareExpression(compareGreaterThanEqualMethod, expression);
                break;
            }
            case 124: {
                this.evaluateCompareExpression(compareLessThanMethod, expression);
                break;
            }
            case 125: {
                this.evaluateCompareExpression(compareLessThanEqualMethod, expression);
                break;
            }
            case 164: {
                this.evaluateLogicalAndExpression(expression);
                break;
            }
            case 162: {
                this.evaluateLogicalOrExpression(expression);
                break;
            }
            case 341: {
                this.evaluateBinaryExpression("and", expression);
                break;
            }
            case 351: {
                this.evaluateBinaryExpressionWithAssignment("and", expression);
                break;
            }
            case 340: {
                this.evaluateBinaryExpression("or", expression);
                break;
            }
            case 350: {
                this.evaluateBinaryExpressionWithAssignment("or", expression);
                break;
            }
            case 342: {
                this.evaluateBinaryExpression("xor", expression);
                break;
            }
            case 352: {
                this.evaluateBinaryExpressionWithAssignment("xor", expression);
                break;
            }
            case 200: {
                this.evaluateBinaryExpression("plus", expression);
                break;
            }
            case 210: {
                this.evaluateBinaryExpressionWithAssignment("plus", expression);
                break;
            }
            case 201: {
                this.evaluateBinaryExpression("minus", expression);
                break;
            }
            case 211: {
                this.evaluateBinaryExpressionWithAssignment("minus", expression);
                break;
            }
            case 202: {
                this.evaluateBinaryExpression("multiply", expression);
                break;
            }
            case 212: {
                this.evaluateBinaryExpressionWithAssignment("multiply", expression);
                break;
            }
            case 203: {
                this.evaluateBinaryExpression("div", expression);
                break;
            }
            case 213: {
                this.evaluateBinaryExpressionWithAssignment("div", expression);
                break;
            }
            case 204: {
                this.evaluateBinaryExpression("intdiv", expression);
                break;
            }
            case 214: {
                this.evaluateBinaryExpressionWithAssignment("intdiv", expression);
                break;
            }
            case 205: {
                this.evaluateBinaryExpression("mod", expression);
                break;
            }
            case 215: {
                this.evaluateBinaryExpressionWithAssignment("mod", expression);
                break;
            }
            case 206: {
                this.evaluateBinaryExpression("power", expression);
                break;
            }
            case 216: {
                this.evaluateBinaryExpressionWithAssignment("power", expression);
                break;
            }
            case 280: {
                this.evaluateBinaryExpression("leftShift", expression);
                break;
            }
            case 285: {
                this.evaluateBinaryExpressionWithAssignment("leftShift", expression);
                break;
            }
            case 281: {
                this.evaluateBinaryExpression("rightShift", expression);
                break;
            }
            case 286: {
                this.evaluateBinaryExpressionWithAssignment("rightShift", expression);
                break;
            }
            case 282: {
                this.evaluateBinaryExpression("rightShiftUnsigned", expression);
                break;
            }
            case 287: {
                this.evaluateBinaryExpressionWithAssignment("rightShiftUnsigned", expression);
                break;
            }
            case 544: {
                this.evaluateInstanceof(expression);
                break;
            }
            case 90: {
                this.evaluateCompareExpression(findRegexMethod, expression);
                break;
            }
            case 94: {
                this.evaluateCompareExpression(matchRegexMethod, expression);
                break;
            }
            case 30: {
                if (this.controller.getCompileStack().isLHS()) {
                    this.evaluateEqual(expression, false);
                    break;
                }
                this.evaluateBinaryExpression("getAt", expression);
                break;
            }
            case 573: {
                this.evaluateCompareExpression(isCaseMethod, expression);
                break;
            }
            case 121: 
            case 122: {
                Token op = expression.getOperation();
                SyntaxException cause = new SyntaxException("Operator " + op + " not supported", op.getStartLine(), op.getStartColumn(), op.getStartLine(), op.getStartColumn() + 3);
                throw new GroovyRuntimeException(cause);
            }
            default: {
                throw new GroovyBugError("Operation: " + expression.getOperation() + " not supported");
            }
        }
    }

    protected void assignToArray(Expression parent, Expression receiver, Expression index, Expression rhsValueLoader) {
        ArgumentListExpression ae = new ArgumentListExpression(index, rhsValueLoader);
        this.controller.getInvocationWriter().makeCall(parent, receiver, new ConstantExpression("putAt"), ae, InvocationWriter.invokeMethod, false, false, false);
        this.controller.getOperandStack().pop();
        rhsValueLoader.visit(this.controller.getAcg());
    }

    private static boolean isNull(Expression exp) {
        if (exp instanceof ConstantExpression) {
            return ((ConstantExpression)exp).getValue() == null;
        }
        return false;
    }

    public void evaluateEqual(BinaryExpression expression, boolean defineVariable) {
        int rhsValueId;
        boolean directAssignment;
        ClassNode rhsType;
        AsmClassGenerator acg = this.controller.getAcg();
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        Expression rightExpression = expression.getRightExpression();
        Expression leftExpression = expression.getLeftExpression();
        ClassNode lhsType = this.controller.getTypeChooser().resolveType(leftExpression, this.controller.getClassNode());
        if (defineVariable && rightExpression instanceof EmptyExpression && !(leftExpression instanceof TupleExpression)) {
            VariableExpression ve = (VariableExpression)leftExpression;
            BytecodeVariable var = compileStack.defineVariable(ve, this.controller.getTypeChooser().resolveType(ve, this.controller.getClassNode()), false);
            operandStack.loadOrStoreVariable(var, false);
            return;
        }
        if (rightExpression instanceof ListExpression && lhsType.isArray()) {
            ListExpression list = (ListExpression)rightExpression;
            ArrayExpression array = new ArrayExpression(lhsType.getComponentType(), list.getExpressions());
            array.setSourcePosition(list);
            array.visit(acg);
        } else if (rightExpression instanceof EmptyExpression) {
            rhsType = leftExpression.getType();
            this.loadInitValue(rhsType);
        } else {
            rightExpression.visit(acg);
        }
        rhsType = operandStack.getTopOperand();
        boolean bl = directAssignment = defineVariable && !(leftExpression instanceof TupleExpression);
        if (directAssignment) {
            VariableExpression var = (VariableExpression)leftExpression;
            if (var.isClosureSharedVariable() && ClassHelper.isPrimitiveType(rhsType)) {
                rhsType = ClassHelper.getWrapper(rhsType);
                operandStack.box();
            }
            if (var.isClosureSharedVariable() && ClassHelper.isPrimitiveType(var.getOriginType()) && BinaryExpressionHelper.isNull(rightExpression)) {
                operandStack.doGroovyCast(var.getOriginType());
                operandStack.box();
                operandStack.doGroovyCast(lhsType);
            }
            if (!ClassHelper.isPrimitiveType(lhsType) && BinaryExpressionHelper.isNull(rightExpression)) {
                operandStack.replace(lhsType);
            } else {
                operandStack.doGroovyCast(lhsType);
            }
            rhsType = lhsType;
            rhsValueId = compileStack.defineVariable(var, lhsType, true).getIndex();
        } else {
            rhsValueId = compileStack.defineTemporaryVariable("$rhs", rhsType, true);
        }
        VariableSlotLoader rhsValueLoader = new VariableSlotLoader(rhsType, rhsValueId, operandStack);
        if (leftExpression instanceof BinaryExpression) {
            BinaryExpression leftBinExpr = (BinaryExpression)leftExpression;
            if (leftBinExpr.getOperation().getType() == 30) {
                this.assignToArray(expression, leftBinExpr.getLeftExpression(), leftBinExpr.getRightExpression(), rhsValueLoader);
            }
            compileStack.removeVar(rhsValueId);
            return;
        }
        compileStack.pushLHS(true);
        if (leftExpression instanceof TupleExpression) {
            TupleExpression tuple = (TupleExpression)leftExpression;
            int i = 0;
            for (Expression e : tuple.getExpressions()) {
                VariableExpression var = (VariableExpression)e;
                MethodCallExpression call = new MethodCallExpression((Expression)rhsValueLoader, "getAt", (Expression)new ArgumentListExpression(new ConstantExpression(i)));
                call.visit(acg);
                ++i;
                if (defineVariable) {
                    operandStack.doGroovyCast(var);
                    compileStack.defineVariable(var, true);
                    operandStack.remove(1);
                    continue;
                }
                acg.visitVariableExpression(var);
            }
        } else {
            if (defineVariable) {
                rhsValueLoader.visit(acg);
                operandStack.remove(1);
                compileStack.popLHS();
                return;
            }
            int mark = operandStack.getStackLength();
            rhsValueLoader.visit(acg);
            TypeChooser typeChooser = this.controller.getTypeChooser();
            ClassNode targetType = typeChooser.resolveType(leftExpression, this.controller.getClassNode());
            operandStack.doGroovyCast(targetType);
            leftExpression.visit(acg);
            operandStack.remove(operandStack.getStackLength() - mark);
        }
        compileStack.popLHS();
        rhsValueLoader.visit(acg);
        compileStack.removeVar(rhsValueId);
    }

    private void loadInitValue(ClassNode type) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (ClassHelper.isPrimitiveType(type)) {
            mv.visitLdcInsn(0);
        } else {
            mv.visitInsn(1);
        }
        this.controller.getOperandStack().push(type);
    }

    protected void evaluateCompareExpression(MethodCaller compareMethod, BinaryExpression expression) {
        Expression leftExp = expression.getLeftExpression();
        TypeChooser typeChooser = this.controller.getTypeChooser();
        ClassNode cn = this.controller.getClassNode();
        ClassNode leftType = typeChooser.resolveType(leftExp, cn);
        Expression rightExp = expression.getRightExpression();
        ClassNode rightType = typeChooser.resolveType(rightExp, cn);
        boolean done = false;
        if (ClassHelper.isPrimitiveType(leftType) && ClassHelper.isPrimitiveType(rightType)) {
            BinaryExpressionMultiTypeDispatcher helper = new BinaryExpressionMultiTypeDispatcher(this.getController());
            done = helper.doPrimitiveCompare(leftType, rightType, expression);
        }
        if (!done) {
            AsmClassGenerator acg = this.controller.getAcg();
            OperandStack operandStack = this.controller.getOperandStack();
            leftExp.visit(acg);
            operandStack.box();
            rightExp.visit(acg);
            operandStack.box();
            compareMethod.call(this.controller.getMethodVisitor());
            ClassNode resType = ClassHelper.boolean_TYPE;
            if (compareMethod == findRegexMethod) {
                resType = ClassHelper.OBJECT_TYPE;
            }
            operandStack.replace(resType, 2);
        }
    }

    private void evaluateCompareTo(BinaryExpression expression) {
        Expression leftExpression = expression.getLeftExpression();
        AsmClassGenerator acg = this.controller.getAcg();
        OperandStack operandStack = this.controller.getOperandStack();
        leftExpression.visit(acg);
        operandStack.box();
        Expression rightExpression = expression.getRightExpression();
        rightExpression.visit(acg);
        operandStack.box();
        compareToMethod.call(this.controller.getMethodVisitor());
        operandStack.replace(ClassHelper.Integer_TYPE, 2);
    }

    private void evaluateLogicalAndExpression(BinaryExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        AsmClassGenerator acg = this.controller.getAcg();
        OperandStack operandStack = this.controller.getOperandStack();
        expression.getLeftExpression().visit(acg);
        operandStack.doGroovyCast(ClassHelper.boolean_TYPE);
        Label falseCase = operandStack.jump(153);
        expression.getRightExpression().visit(acg);
        operandStack.doGroovyCast(ClassHelper.boolean_TYPE);
        operandStack.jump(153, falseCase);
        ConstantExpression.PRIM_TRUE.visit(acg);
        Label trueCase = new Label();
        mv.visitJumpInsn(167, trueCase);
        mv.visitLabel(falseCase);
        ConstantExpression.PRIM_FALSE.visit(acg);
        mv.visitLabel(trueCase);
        operandStack.remove(1);
    }

    private void evaluateLogicalOrExpression(BinaryExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        AsmClassGenerator acg = this.controller.getAcg();
        OperandStack operandStack = this.controller.getOperandStack();
        Label end = new Label();
        expression.getLeftExpression().visit(acg);
        operandStack.doGroovyCast(ClassHelper.boolean_TYPE);
        Label trueCase = operandStack.jump(154);
        expression.getRightExpression().visit(acg);
        operandStack.doGroovyCast(ClassHelper.boolean_TYPE);
        Label falseCase = operandStack.jump(153);
        mv.visitLabel(trueCase);
        ConstantExpression.PRIM_TRUE.visit(acg);
        operandStack.jump(167, end);
        mv.visitLabel(falseCase);
        ConstantExpression.PRIM_FALSE.visit(acg);
        mv.visitLabel(end);
    }

    protected void evaluateBinaryExpression(String message, BinaryExpression binExp) {
        CompileStack compileStack = this.controller.getCompileStack();
        Expression receiver = binExp.getLeftExpression();
        Expression arguments = binExp.getRightExpression();
        compileStack.pushLHS(false);
        this.controller.getInvocationWriter().makeSingleArgumentCall(receiver, message, arguments);
        compileStack.popLHS();
    }

    protected void evaluateArrayAssignmentWithOperator(String method, BinaryExpression expression, BinaryExpression leftBinExpr) {
        CompileStack compileStack = this.getController().getCompileStack();
        AsmClassGenerator acg = this.getController().getAcg();
        OperandStack os = this.getController().getOperandStack();
        ExpressionAsVariableSlot subscript = new ExpressionAsVariableSlot(this.controller, leftBinExpr.getRightExpression(), "subscript");
        ExpressionAsVariableSlot receiver = new ExpressionAsVariableSlot(this.controller, leftBinExpr.getLeftExpression(), "receiver");
        MethodCallExpression getAt = new MethodCallExpression((Expression)receiver, "getAt", (Expression)new ArgumentListExpression(subscript));
        MethodCallExpression operation = new MethodCallExpression((Expression)getAt, method, expression.getRightExpression());
        ExpressionAsVariableSlot ret = new ExpressionAsVariableSlot(this.controller, operation, "ret");
        MethodCallExpression putAt = new MethodCallExpression((Expression)receiver, "putAt", (Expression)new ArgumentListExpression(subscript, ret));
        putAt.visit(acg);
        os.pop();
        os.load(ret.getType(), ret.getIndex());
        compileStack.removeVar(ret.getIndex());
        compileStack.removeVar(subscript.getIndex());
        compileStack.removeVar(receiver.getIndex());
    }

    protected void evaluateBinaryExpressionWithAssignment(String method, BinaryExpression expression) {
        BinaryExpression leftBinExpr;
        Expression leftExpression = expression.getLeftExpression();
        AsmClassGenerator acg = this.controller.getAcg();
        OperandStack operandStack = this.controller.getOperandStack();
        if (leftExpression instanceof BinaryExpression && (leftBinExpr = (BinaryExpression)leftExpression).getOperation().getType() == 30) {
            this.evaluateArrayAssignmentWithOperator(method, expression, leftBinExpr);
            return;
        }
        this.evaluateBinaryExpression(method, expression);
        operandStack.dup();
        this.controller.getCompileStack().pushLHS(true);
        leftExpression.visit(acg);
        this.controller.getCompileStack().popLHS();
    }

    private void evaluateInstanceof(BinaryExpression expression) {
        OperandStack operandStack = this.controller.getOperandStack();
        expression.getLeftExpression().visit(this.controller.getAcg());
        operandStack.box();
        Expression rightExp = expression.getRightExpression();
        if (!(rightExp instanceof ClassExpression)) {
            throw new RuntimeException("Right hand side of the instanceof keyword must be a class name, not: " + rightExp);
        }
        ClassExpression classExp = (ClassExpression)rightExp;
        ClassNode classType = classExp.getType();
        String classInternalName = BytecodeHelper.getClassInternalName(classType);
        this.controller.getMethodVisitor().visitTypeInsn(193, classInternalName);
        operandStack.replace(ClassHelper.boolean_TYPE);
    }

    public MethodCaller getIsCaseMethod() {
        return isCaseMethod;
    }

    private void evaluatePostfixMethod(int op, String method, Expression expression, Expression orig) {
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        VariableSlotLoader usesSubscript = this.loadWithSubscript(expression);
        operandStack.dup();
        ClassNode expressionType = operandStack.getTopOperand();
        int tempIdx = compileStack.defineTemporaryVariable("postfix_" + method, expressionType, true);
        this.execMethodAndStoreForSubscriptOperator(op, method, expression, usesSubscript, orig);
        operandStack.pop();
        operandStack.load(expressionType, tempIdx);
        compileStack.removeVar(tempIdx);
        if (usesSubscript != null) {
            compileStack.removeVar(usesSubscript.getIndex());
        }
    }

    public void evaluatePostfixMethod(PostfixExpression expression) {
        int op = expression.getOperation().getType();
        switch (op) {
            case 250: {
                this.evaluatePostfixMethod(op, "next", expression.getExpression(), expression);
                break;
            }
            case 260: {
                this.evaluatePostfixMethod(op, "previous", expression.getExpression(), expression);
            }
        }
    }

    public void evaluatePrefixMethod(PrefixExpression expression) {
        int type = expression.getOperation().getType();
        switch (type) {
            case 250: {
                this.evaluatePrefixMethod(type, "next", expression.getExpression(), expression);
                break;
            }
            case 260: {
                this.evaluatePrefixMethod(type, "previous", expression.getExpression(), expression);
            }
        }
    }

    private void evaluatePrefixMethod(int op, String method, Expression expression, Expression orig) {
        VariableSlotLoader usesSubscript = this.loadWithSubscript(expression);
        this.execMethodAndStoreForSubscriptOperator(op, method, expression, usesSubscript, orig);
        if (usesSubscript != null) {
            this.controller.getCompileStack().removeVar(usesSubscript.getIndex());
        }
    }

    private VariableSlotLoader loadWithSubscript(Expression expression) {
        BinaryExpression be;
        OperandStack operandStack = this.controller.getOperandStack();
        if (expression instanceof BinaryExpression && (be = (BinaryExpression)expression).getOperation().getType() == 30) {
            Expression subscript = be.getRightExpression();
            subscript.visit(this.controller.getAcg());
            ClassNode subscriptType = operandStack.getTopOperand();
            int id = this.controller.getCompileStack().defineTemporaryVariable("$subscript", subscriptType, true);
            VariableSlotLoader subscriptExpression = new VariableSlotLoader(subscriptType, id, operandStack);
            BinaryExpression newBe = new BinaryExpression(be.getLeftExpression(), be.getOperation(), subscriptExpression);
            newBe.copyNodeMetaData(be);
            newBe.setSourcePosition(be);
            newBe.visit(this.controller.getAcg());
            return subscriptExpression;
        }
        expression.visit(this.controller.getAcg());
        return null;
    }

    private void execMethodAndStoreForSubscriptOperator(int op, String method, Expression expression, VariableSlotLoader usesSubscript, Expression orig) {
        OperandStack operandStack = this.controller.getOperandStack();
        this.writePostOrPrefixMethod(op, method, expression, orig);
        if (usesSubscript != null) {
            CompileStack compileStack = this.controller.getCompileStack();
            BinaryExpression be = (BinaryExpression)expression;
            ClassNode methodResultType = operandStack.getTopOperand();
            int resultIdx = compileStack.defineTemporaryVariable("postfix_" + method, methodResultType, true);
            VariableSlotLoader methodResultLoader = new VariableSlotLoader(methodResultType, resultIdx, operandStack);
            this.assignToArray(be, be.getLeftExpression(), usesSubscript, methodResultLoader);
            compileStack.removeVar(resultIdx);
        } else if (expression instanceof VariableExpression || expression instanceof FieldExpression || expression instanceof PropertyExpression) {
            operandStack.dup();
            this.controller.getCompileStack().pushLHS(true);
            expression.visit(this.controller.getAcg());
            this.controller.getCompileStack().popLHS();
        }
    }

    protected void writePostOrPrefixMethod(int op, String method, Expression expression, Expression orig) {
        final OperandStack operandStack = this.controller.getOperandStack();
        ClassNode BEType = this.controller.getTypeChooser().resolveType(expression, this.controller.getClassNode());
        BytecodeExpression callSiteReceiverSwap = new BytecodeExpression(BEType){

            @Override
            public void visit(MethodVisitor mv) {
                operandStack.push(ClassHelper.OBJECT_TYPE);
                operandStack.swap();
                this.setType(operandStack.getTopOperand());
                operandStack.remove(2);
            }
        };
        this.controller.getCallSiteWriter().makeCallSite(callSiteReceiverSwap, method, MethodCallExpression.NO_ARGUMENTS, false, false, false, false);
    }

    private void evaluateElvisOperatorExpression(ElvisOperatorExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        TypeChooser typeChooser = this.controller.getTypeChooser();
        Expression boolPart = expression.getBooleanExpression().getExpression();
        Expression falsePart = expression.getFalseExpression();
        ClassNode truePartType = typeChooser.resolveType(boolPart, this.controller.getClassNode());
        ClassNode falsePartType = typeChooser.resolveType(falsePart, this.controller.getClassNode());
        ClassNode common = WideningCategories.lowestUpperBound(truePartType, falsePartType);
        int mark = operandStack.getStackLength();
        boolPart.visit(this.controller.getAcg());
        operandStack.dup();
        if (ClassHelper.isPrimitiveType(truePartType) && !ClassHelper.isPrimitiveType(operandStack.getTopOperand())) {
            truePartType = ClassHelper.getWrapper(truePartType);
        }
        int retValueId = compileStack.defineTemporaryVariable("$t", truePartType, true);
        operandStack.castToBool(mark, true);
        Label l0 = operandStack.jump(153);
        operandStack.load(truePartType, retValueId);
        operandStack.doGroovyCast(common);
        Label l1 = new Label();
        mv.visitJumpInsn(167, l1);
        mv.visitLabel(l0);
        falsePart.visit(this.controller.getAcg());
        operandStack.doGroovyCast(common);
        mv.visitLabel(l1);
        compileStack.removeVar(retValueId);
        this.controller.getOperandStack().replace(common, 2);
    }

    private void evaluateNormalTernary(TernaryExpression expression) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        TypeChooser typeChooser = this.controller.getTypeChooser();
        BooleanExpression boolPart = expression.getBooleanExpression();
        Expression truePart = expression.getTrueExpression();
        Expression falsePart = expression.getFalseExpression();
        ClassNode truePartType = typeChooser.resolveType(truePart, this.controller.getClassNode());
        ClassNode falsePartType = typeChooser.resolveType(falsePart, this.controller.getClassNode());
        ClassNode common = WideningCategories.lowestUpperBound(truePartType, falsePartType);
        int mark = operandStack.getStackLength();
        ((ASTNode)boolPart).visit(this.controller.getAcg());
        operandStack.castToBool(mark, true);
        Label l0 = operandStack.jump(153);
        truePart.visit(this.controller.getAcg());
        operandStack.doGroovyCast(common);
        Label l1 = new Label();
        mv.visitJumpInsn(167, l1);
        mv.visitLabel(l0);
        falsePart.visit(this.controller.getAcg());
        operandStack.doGroovyCast(common);
        mv.visitLabel(l1);
        this.controller.getOperandStack().replace(common, 2);
    }

    public void evaluateTernary(TernaryExpression expression) {
        if (expression instanceof ElvisOperatorExpression) {
            this.evaluateElvisOperatorExpression((ElvisOperatorExpression)expression);
        } else {
            this.evaluateNormalTernary(expression);
        }
    }
}

