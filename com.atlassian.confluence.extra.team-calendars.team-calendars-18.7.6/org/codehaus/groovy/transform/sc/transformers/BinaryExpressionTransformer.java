/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.asm.sc.StaticPropertyAccessHelper;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesTypeChooser;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.sc.ListOfExpressionsExpression;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.sc.transformers.CompareIdentityExpression;
import org.codehaus.groovy.transform.sc.transformers.CompareToNullExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class BinaryExpressionTransformer {
    private static final MethodNode COMPARE_TO_METHOD = ClassHelper.COMPARABLE_TYPE.getMethods("compareTo").get(0);
    private static final ConstantExpression CONSTANT_ZERO = new ConstantExpression(0, true);
    private static final ConstantExpression CONSTANT_MINUS_ONE = new ConstantExpression(-1, true);
    private static final ConstantExpression CONSTANT_ONE = new ConstantExpression(1, true);
    private int tmpVarCounter = 0;
    private final StaticCompilationTransformer staticCompilationTransformer;

    public BinaryExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.staticCompilationTransformer = staticCompilationTransformer;
    }

    Expression transformBinaryExpression(BinaryExpression bin) {
        MethodNode directMCT;
        Expression optimized;
        if (bin instanceof DeclarationExpression && (optimized = BinaryExpressionTransformer.transformDeclarationExpression(bin)) != null) {
            return optimized;
        }
        Object[] list = (Object[])bin.getNodeMetaData((Object)StaticCompilationMetadataKeys.BINARY_EXP_TARGET);
        Token operation = bin.getOperation();
        int operationType = operation.getType();
        Expression rightExpression = bin.getRightExpression();
        Expression leftExpression = bin.getLeftExpression();
        if (bin instanceof DeclarationExpression && leftExpression instanceof VariableExpression) {
            ClassNode declarationType = ((VariableExpression)leftExpression).getOriginType();
            if (rightExpression instanceof ConstantExpression) {
                ConstantExpression constant;
                ClassNode unwrapper = ClassHelper.getUnwrapper(declarationType);
                ClassNode wrapper = ClassHelper.getWrapper(declarationType);
                if (!rightExpression.getType().equals(declarationType) && wrapper.isDerivedFrom(ClassHelper.Number_TYPE) && WideningCategories.isDoubleCategory(unwrapper) && (constant = (ConstantExpression)rightExpression).getValue() != null) {
                    return BinaryExpressionTransformer.optimizeConstantInitialization(bin, operation, constant, leftExpression, declarationType);
                }
            }
        }
        if (operationType == 100 && leftExpression instanceof PropertyExpression && (directMCT = (MethodNode)leftExpression.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET)) != null) {
            return this.transformPropertyAssignmentToSetterCall((PropertyExpression)leftExpression, rightExpression, directMCT);
        }
        if (operationType == 123 || operationType == 120) {
            ASTNode compareToNullExpression = null;
            if (BinaryExpressionTransformer.isNullConstant(leftExpression)) {
                compareToNullExpression = new CompareToNullExpression(this.staticCompilationTransformer.transform(rightExpression), operationType == 123);
            } else if (BinaryExpressionTransformer.isNullConstant(rightExpression)) {
                compareToNullExpression = new CompareToNullExpression(this.staticCompilationTransformer.transform(leftExpression), operationType == 123);
            }
            if (compareToNullExpression != null) {
                compareToNullExpression.setSourcePosition(bin);
                return compareToNullExpression;
            }
        } else if (operationType == 573) {
            return this.convertInOperatorToTernary(bin, rightExpression, leftExpression);
        }
        if (list != null) {
            Expression right;
            ClassNode rightType;
            ClassNode classNode;
            StaticTypesTypeChooser typeChooser;
            ClassNode leftType;
            if (operationType == 128 && (leftType = (typeChooser = this.staticCompilationTransformer.getTypeChooser()).resolveType(leftExpression, classNode = this.staticCompilationTransformer.getClassNode())).implementsInterface(ClassHelper.COMPARABLE_TYPE) && (rightType = typeChooser.resolveType(rightExpression, classNode)).implementsInterface(ClassHelper.COMPARABLE_TYPE)) {
                Expression left = this.staticCompilationTransformer.transform(leftExpression);
                Expression right2 = this.staticCompilationTransformer.transform(rightExpression);
                MethodCallExpression call = new MethodCallExpression(left, "compareTo", (Expression)new ArgumentListExpression(right2));
                call.setImplicitThis(false);
                call.setMethodTarget(COMPARE_TO_METHOD);
                call.setSourcePosition(bin);
                CompareIdentityExpression compareIdentity = new CompareIdentityExpression(left, right2);
                compareIdentity.putNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE, ClassHelper.boolean_TYPE);
                TernaryExpression result = new TernaryExpression(new BooleanExpression(compareIdentity), CONSTANT_ZERO, new TernaryExpression(new BooleanExpression(new CompareToNullExpression(left, true)), CONSTANT_MINUS_ONE, new TernaryExpression(new BooleanExpression(new CompareToNullExpression(right2, true)), CONSTANT_ONE, call)));
                compareIdentity.putNodeMetaData((Object)StaticTypesMarker.INFERRED_RETURN_TYPE, ClassHelper.int_TYPE);
                result.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, ClassHelper.int_TYPE);
                TernaryExpression expr = (TernaryExpression)result.getFalseExpression();
                expr.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, ClassHelper.int_TYPE);
                expr.getFalseExpression().putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, ClassHelper.int_TYPE);
                return result;
            }
            boolean isAssignment = StaticTypeCheckingSupport.isAssignment(operationType);
            MethodNode node = (MethodNode)list[0];
            String name = (String)list[1];
            Expression left = this.staticCompilationTransformer.transform(leftExpression);
            BinaryExpression optimized2 = BinaryExpressionTransformer.tryOptimizeCharComparison(left, right = this.staticCompilationTransformer.transform(rightExpression), bin);
            if (optimized2 != null) {
                optimized2.removeNodeMetaData((Object)StaticCompilationMetadataKeys.BINARY_EXP_TARGET);
                return this.transformBinaryExpression(optimized2);
            }
            MethodCallExpression call = new MethodCallExpression(left, name, (Expression)new ArgumentListExpression(right));
            call.setImplicitThis(false);
            call.setMethodTarget(node);
            MethodNode adapter = StaticCompilationTransformer.BYTECODE_BINARY_ADAPTERS.get(operationType);
            if (adapter != null) {
                ClassExpression sba = new ClassExpression(StaticCompilationTransformer.BYTECODE_ADAPTER_CLASS);
                call = new MethodCallExpression((Expression)sba, "compareEquals", (Expression)new ArgumentListExpression(left, right));
                call.setMethodTarget(adapter);
                call.setImplicitThis(false);
            }
            call.setSourcePosition(bin);
            if (!isAssignment) {
                return call;
            }
            return new BinaryExpression(left, Token.newSymbol("=", operation.getStartLine(), operation.getStartColumn()), call);
        }
        if (bin.getOperation().getType() == 100 && leftExpression instanceof TupleExpression && rightExpression instanceof ListExpression) {
            ListOfExpressionsExpression cle = new ListOfExpressionsExpression();
            boolean isDeclaration = bin instanceof DeclarationExpression;
            List<Expression> leftExpressions = ((TupleExpression)leftExpression).getExpressions();
            List<Expression> rightExpressions = ((ListExpression)rightExpression).getExpressions();
            Iterator<Expression> leftIt = leftExpressions.iterator();
            Iterator<Expression> rightIt = rightExpressions.iterator();
            if (isDeclaration) {
                while (leftIt.hasNext()) {
                    Expression left = leftIt.next();
                    if (!rightIt.hasNext()) continue;
                    Expression right = rightIt.next();
                    DeclarationExpression bexp = new DeclarationExpression(left, bin.getOperation(), right);
                    bexp.setSourcePosition(right);
                    cle.addExpression(bexp);
                }
            } else {
                int size = rightExpressions.size();
                ArrayList<DeclarationExpression> tmpAssignments = new ArrayList<DeclarationExpression>(size);
                ArrayList<BinaryExpression> finalAssignments = new ArrayList<BinaryExpression>(size);
                for (int i = 0; i < Math.min(size, leftExpressions.size()); ++i) {
                    Expression expression = leftIt.next();
                    Expression right = rightIt.next();
                    VariableExpression tmpVar = new VariableExpression("$tmpVar$" + this.tmpVarCounter++);
                    BinaryExpression bexp = new DeclarationExpression(tmpVar, bin.getOperation(), right);
                    bexp.setSourcePosition(right);
                    tmpAssignments.add((DeclarationExpression)bexp);
                    bexp = new BinaryExpression(expression, bin.getOperation(), new VariableExpression(tmpVar));
                    bexp.setSourcePosition(expression);
                    finalAssignments.add(bexp);
                }
                for (Expression expression : tmpAssignments) {
                    cle.addExpression(expression);
                }
                for (Expression expression : finalAssignments) {
                    cle.addExpression(expression);
                }
            }
            return this.staticCompilationTransformer.transform(cle);
        }
        return this.staticCompilationTransformer.superTransform(bin);
    }

    private static BinaryExpression tryOptimizeCharComparison(Expression left, Expression right, BinaryExpression bin) {
        int op = bin.getOperation().getType();
        if (StaticTypeCheckingSupport.isCompareToBoolean(op) || op == 123 || op == 120) {
            Character cLeft = BinaryExpressionTransformer.tryCharConstant(left);
            Character cRight = BinaryExpressionTransformer.tryCharConstant(right);
            if (cLeft != null || cRight != null) {
                Expression oLeft = cLeft == null ? left : new ConstantExpression(cLeft, true);
                oLeft.setSourcePosition(left);
                Expression oRight = cRight == null ? right : new ConstantExpression(cRight, true);
                oRight.setSourcePosition(right);
                bin.setLeftExpression(oLeft);
                bin.setRightExpression(oRight);
                return bin;
            }
        }
        return null;
    }

    private static Character tryCharConstant(Expression expr) {
        String val;
        ConstantExpression ce;
        if (expr instanceof ConstantExpression && ClassHelper.STRING_TYPE.equals((ce = (ConstantExpression)expr).getType()) && (val = (String)ce.getValue()) != null && val.length() == 1) {
            return Character.valueOf(val.charAt(0));
        }
        return null;
    }

    private static Expression transformDeclarationExpression(BinaryExpression bin) {
        String text;
        Expression rightExpression;
        Expression leftExpression = bin.getLeftExpression();
        if (leftExpression instanceof VariableExpression && ClassHelper.char_TYPE.equals(((VariableExpression)leftExpression).getOriginType()) && (rightExpression = bin.getRightExpression()) instanceof ConstantExpression && ClassHelper.STRING_TYPE.equals(rightExpression.getType()) && (text = (String)((ConstantExpression)rightExpression).getValue()).length() == 1) {
            ConstantExpression ce = new ConstantExpression(Character.valueOf(text.charAt(0)), true);
            ce.setSourcePosition(rightExpression);
            bin.setRightExpression(ce);
            return bin;
        }
        return null;
    }

    private Expression convertInOperatorToTernary(BinaryExpression bin, Expression rightExpression, Expression leftExpression) {
        MethodCallExpression call = new MethodCallExpression(rightExpression, "isCase", leftExpression);
        call.setMethodTarget((MethodNode)bin.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET));
        call.setSourcePosition(bin);
        call.copyNodeMetaData(bin);
        TernaryExpression tExp = new TernaryExpression(new BooleanExpression(new BinaryExpression(rightExpression, Token.newSymbol("==", -1, -1), new ConstantExpression(null))), new BinaryExpression(leftExpression, Token.newSymbol("==", -1, -1), new ConstantExpression(null)), call);
        return this.staticCompilationTransformer.transform(tExp);
    }

    private static DeclarationExpression optimizeConstantInitialization(BinaryExpression originalDeclaration, Token operation, ConstantExpression constant, Expression leftExpression, ClassNode declarationType) {
        ConstantExpression cexp = new ConstantExpression(BinaryExpressionTransformer.convertConstant((Number)constant.getValue(), ClassHelper.getWrapper(declarationType)), true);
        cexp.setType(declarationType);
        cexp.setSourcePosition(constant);
        DeclarationExpression result = new DeclarationExpression(leftExpression, operation, (Expression)cexp);
        result.setSourcePosition(originalDeclaration);
        result.copyNodeMetaData(originalDeclaration);
        return result;
    }

    private static Object convertConstant(Number source, ClassNode target) {
        if (ClassHelper.Byte_TYPE.equals(target)) {
            return source.byteValue();
        }
        if (ClassHelper.Short_TYPE.equals(target)) {
            return source.shortValue();
        }
        if (ClassHelper.Integer_TYPE.equals(target)) {
            return source.intValue();
        }
        if (ClassHelper.Long_TYPE.equals(target)) {
            return source.longValue();
        }
        if (ClassHelper.Float_TYPE.equals(target)) {
            return Float.valueOf(source.floatValue());
        }
        if (ClassHelper.Double_TYPE.equals(target)) {
            return source.doubleValue();
        }
        if (ClassHelper.BigInteger_TYPE.equals(target)) {
            return DefaultGroovyMethods.asType(source, BigInteger.class);
        }
        if (ClassHelper.BigDecimal_TYPE.equals(target)) {
            return DefaultGroovyMethods.asType(source, BigDecimal.class);
        }
        throw new IllegalArgumentException("Unsupported conversion");
    }

    private Expression transformPropertyAssignmentToSetterCall(PropertyExpression leftExpression, Expression rightExpression, MethodNode directMCT) {
        Expression arg = this.staticCompilationTransformer.transform(rightExpression);
        return StaticPropertyAccessHelper.transformToSetterCall(leftExpression.getObjectExpression(), directMCT, arg, false, leftExpression.isSafe(), false, true, leftExpression);
    }

    protected static boolean isNullConstant(Expression expression) {
        return expression instanceof ConstantExpression && ((ConstantExpression)expression).getValue() == null;
    }

    static {
        CONSTANT_ZERO.setType(ClassHelper.int_TYPE);
        CONSTANT_ONE.setType(ClassHelper.int_TYPE);
        CONSTANT_MINUS_ONE.setType(ClassHelper.int_TYPE);
    }
}

