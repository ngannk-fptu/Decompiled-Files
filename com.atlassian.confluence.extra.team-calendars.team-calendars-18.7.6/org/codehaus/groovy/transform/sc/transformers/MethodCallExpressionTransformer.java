/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.classgen.asm.MopWriter;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.sc.transformers.CompareIdentityExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class MethodCallExpressionTransformer {
    private static final ClassNode DGM_CLASSNODE = ClassHelper.make(DefaultGroovyMethods.class);
    private final StaticCompilationTransformer staticCompilationTransformer;

    public MethodCallExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.staticCompilationTransformer = staticCompilationTransformer;
    }

    Expression transformMethodCallExpression(MethodCallExpression expr) {
        FieldNode field;
        Expression trn = MethodCallExpressionTransformer.tryTransformIsToCompareIdentity(expr);
        if (trn != null) {
            return trn;
        }
        ClassNode superCallReceiver = (ClassNode)expr.getNodeMetaData((Object)StaticTypesMarker.SUPER_MOP_METHOD_REQUIRED);
        if (superCallReceiver != null) {
            return this.transformMethodCallExpression(MethodCallExpressionTransformer.transformToMopSuperCall(superCallReceiver, expr));
        }
        Expression objectExpression = expr.getObjectExpression();
        ClassNode type = this.staticCompilationTransformer.getTypeChooser().resolveType(objectExpression, this.staticCompilationTransformer.getClassNode());
        if (MethodCallExpressionTransformer.isCallOnClosure(expr) && (field = this.staticCompilationTransformer.getClassNode().getField(expr.getMethodAsString())) != null) {
            VariableExpression vexp = new VariableExpression(field);
            MethodCallExpression result = new MethodCallExpression((Expression)vexp, "call", this.staticCompilationTransformer.transform(expr.getArguments()));
            result.setImplicitThis(false);
            result.setSourcePosition(expr);
            result.setSafe(expr.isSafe());
            result.setSpreadSafe(expr.isSpreadSafe());
            result.setMethodTarget(StaticTypeCheckingVisitor.CLOSURE_CALL_VARGS);
            result.copyNodeMetaData(expr);
            return result;
        }
        if (type != null && type.isArray()) {
            List<Expression> argList;
            Expression arguments;
            String method = expr.getMethodAsString();
            ClassNode componentType = type.getComponentType();
            if ("getAt".equals(method)) {
                List<Expression> argList2;
                Expression arguments2 = expr.getArguments();
                if (arguments2 instanceof TupleExpression && (argList2 = ((TupleExpression)arguments2).getExpressions()).size() == 1) {
                    Expression indexExpr = argList2.get(0);
                    ClassNode argType = this.staticCompilationTransformer.getTypeChooser().resolveType(indexExpr, this.staticCompilationTransformer.getClassNode());
                    ClassNode indexType = ClassHelper.getWrapper(argType);
                    if (componentType.isEnum() && ClassHelper.Number_TYPE == indexType) {
                        indexType = ClassHelper.Integer_TYPE;
                    }
                    if (argType != null && ClassHelper.Integer_TYPE == indexType) {
                        BinaryExpression binaryExpression = new BinaryExpression(objectExpression, Token.newSymbol("[", indexExpr.getLineNumber(), indexExpr.getColumnNumber()), indexExpr);
                        binaryExpression.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, componentType);
                        return this.staticCompilationTransformer.transform(binaryExpression);
                    }
                }
            } else if ("putAt".equals(method) && (arguments = expr.getArguments()) instanceof TupleExpression && (argList = ((TupleExpression)arguments).getExpressions()).size() == 2) {
                Expression indexExpr = argList.get(0);
                Expression objExpr = argList.get(1);
                ClassNode argType = this.staticCompilationTransformer.getTypeChooser().resolveType(indexExpr, this.staticCompilationTransformer.getClassNode());
                if (argType != null && ClassHelper.Integer_TYPE == ClassHelper.getWrapper(argType)) {
                    BinaryExpression arrayGet = new BinaryExpression(objectExpression, Token.newSymbol("[", indexExpr.getLineNumber(), indexExpr.getColumnNumber()), indexExpr);
                    arrayGet.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, componentType);
                    BinaryExpression assignment = new BinaryExpression(arrayGet, Token.newSymbol("=", objExpr.getLineNumber(), objExpr.getColumnNumber()), objExpr);
                    return this.staticCompilationTransformer.transform(assignment);
                }
            }
        }
        return this.staticCompilationTransformer.superTransform(expr);
    }

    private static MethodCallExpression transformToMopSuperCall(ClassNode superCallReceiver, MethodCallExpression expr) {
        MethodNode mn = (MethodNode)expr.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        String mopName = MopWriter.getMopMethodName(mn, false);
        MethodNode direct = new MethodNode(mopName, 4097, mn.getReturnType(), mn.getParameters(), mn.getExceptions(), EmptyStatement.INSTANCE);
        direct.setDeclaringClass(superCallReceiver);
        MethodCallExpression result = new MethodCallExpression((Expression)new VariableExpression("this"), mopName, expr.getArguments());
        result.setImplicitThis(true);
        result.setSpreadSafe(false);
        result.setSafe(false);
        result.setSourcePosition(expr);
        result.setMethodTarget(direct);
        return result;
    }

    private static boolean isCallOnClosure(MethodCallExpression expr) {
        MethodNode target = (MethodNode)expr.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        return expr.isImplicitThis() && !"call".equals(expr.getMethodAsString()) && (target == StaticTypeCheckingVisitor.CLOSURE_CALL_VARGS || target == StaticTypeCheckingVisitor.CLOSURE_CALL_NO_ARG || target == StaticTypeCheckingVisitor.CLOSURE_CALL_ONE_ARG);
    }

    private static Expression tryTransformIsToCompareIdentity(MethodCallExpression call) {
        ArgumentListExpression arguments;
        List<Expression> exprs;
        Expression args;
        ClassNode owner;
        if (call.isSafe()) {
            return null;
        }
        MethodNode methodTarget = call.getMethodTarget();
        if (methodTarget instanceof ExtensionMethodNode && "is".equals(methodTarget.getName()) && methodTarget.getParameters().length == 1 && DGM_CLASSNODE.equals(owner = (methodTarget = ((ExtensionMethodNode)methodTarget).getExtensionMethodNode()).getDeclaringClass()) && (args = call.getArguments()) instanceof ArgumentListExpression && (exprs = (arguments = (ArgumentListExpression)args).getExpressions()).size() == 1) {
            CompareIdentityExpression cid = new CompareIdentityExpression(call.getObjectExpression(), exprs.get(0));
            cid.setSourcePosition(call);
            return cid;
        }
        return null;
    }
}

