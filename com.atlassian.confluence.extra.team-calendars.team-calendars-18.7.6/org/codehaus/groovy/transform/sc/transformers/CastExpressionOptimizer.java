/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;

public class CastExpressionOptimizer {
    private final StaticCompilationTransformer transformer;

    public CastExpressionOptimizer(StaticCompilationTransformer staticCompilationTransformer) {
        this.transformer = staticCompilationTransformer;
    }

    public Expression transformCastExpression(CastExpression cast) {
        String val;
        ConstantExpression ce;
        Expression expression;
        if (cast.isCoerce()) {
            Expression expression2 = cast.getExpression();
            ClassNode exprInferredType = this.transformer.getTypeChooser().resolveType(expression2, this.transformer.getClassNode());
            ClassNode castType = cast.getType();
            if (castType.isArray() && expression2 instanceof ListExpression) {
                ArrayExpression arrayExpression = new ArrayExpression(castType.getComponentType(), ((ListExpression)expression2).getExpressions());
                arrayExpression.setSourcePosition(cast);
                return this.transformer.transform(arrayExpression);
            }
            if (CastExpressionOptimizer.isOptimizable(exprInferredType, castType)) {
                CastExpression trn = new CastExpression(castType, this.transformer.transform(expression2));
                trn.setSourcePosition(cast);
                trn.copyNodeMetaData(cast);
                return trn;
            }
        } else if (ClassHelper.char_TYPE.equals(cast.getType()) && (expression = cast.getExpression()) instanceof ConstantExpression && ClassHelper.STRING_TYPE.equals((ce = (ConstantExpression)expression).getType()) && (val = (String)ce.getValue()) != null && val.length() == 1) {
            ConstantExpression result = new ConstantExpression(Character.valueOf(val.charAt(0)), true);
            result.setSourcePosition(cast);
            return result;
        }
        return this.transformer.superTransform(cast);
    }

    private static boolean isOptimizable(ClassNode exprInferredType, ClassNode castType) {
        if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(exprInferredType, castType)) {
            return true;
        }
        return ClassHelper.isPrimitiveType(exprInferredType) && ClassHelper.isPrimitiveType(castType);
    }
}

