/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.math.BigDecimal;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.SourceUnit;

public class AnnotationConstantsVisitor
extends ClassCodeVisitorSupport {
    private SourceUnit source;
    private boolean inAnnotationDef;

    public void visitClass(ClassNode node, SourceUnit source) {
        this.source = source;
        this.inAnnotationDef = node.isAnnotationDefinition();
        super.visitClass(node);
        this.inAnnotationDef = false;
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (!this.inAnnotationDef) {
            return;
        }
        AnnotationConstantsVisitor.visitStatement(node.getFirstStatement(), node.getReturnType());
    }

    private static void visitStatement(Statement statement, ClassNode returnType) {
        if (statement instanceof ReturnStatement) {
            ReturnStatement rs = (ReturnStatement)statement;
            rs.setExpression(AnnotationConstantsVisitor.transformConstantExpression(rs.getExpression(), returnType));
        } else if (statement instanceof ExpressionStatement) {
            ExpressionStatement es = (ExpressionStatement)statement;
            es.setExpression(AnnotationConstantsVisitor.transformConstantExpression(es.getExpression(), returnType));
        }
    }

    private static Expression transformConstantExpression(Expression val, ClassNode returnType) {
        CastExpression castExp;
        Expression castee;
        ClassNode returnWrapperType = ClassHelper.getWrapper(returnType);
        if (val instanceof ConstantExpression) {
            Expression result = AnnotationConstantsVisitor.revertType(val, returnWrapperType);
            if (result != null) {
                return result;
            }
            return val;
        }
        if (val instanceof CastExpression && (castee = (castExp = (CastExpression)val).getExpression()) instanceof ConstantExpression) {
            if (ClassHelper.getWrapper(castee.getType()).isDerivedFrom(returnWrapperType)) {
                return castee;
            }
            Expression result = AnnotationConstantsVisitor.revertType(castee, returnWrapperType);
            if (result != null) {
                return result;
            }
            return castee;
        }
        return val;
    }

    private static Expression revertType(Expression val, ClassNode returnWrapperType) {
        ConstantExpression ce = (ConstantExpression)val;
        if (ClassHelper.Character_TYPE.equals(returnWrapperType) && ClassHelper.STRING_TYPE.equals(val.getType())) {
            return AnnotationConstantsVisitor.configure(val, Verifier.transformToPrimitiveConstantIfPossible((ConstantExpression)val));
        }
        ClassNode valWrapperType = ClassHelper.getWrapper(val.getType());
        if (ClassHelper.Integer_TYPE.equals(valWrapperType)) {
            Integer i = (Integer)ce.getValue();
            if (ClassHelper.Character_TYPE.equals(returnWrapperType)) {
                return AnnotationConstantsVisitor.configure(val, new ConstantExpression(Character.valueOf((char)i.intValue()), true));
            }
            if (ClassHelper.Short_TYPE.equals(returnWrapperType)) {
                return AnnotationConstantsVisitor.configure(val, new ConstantExpression(i.shortValue(), true));
            }
            if (ClassHelper.Byte_TYPE.equals(returnWrapperType)) {
                return AnnotationConstantsVisitor.configure(val, new ConstantExpression(i.byteValue(), true));
            }
        }
        if (ClassHelper.BigDecimal_TYPE.equals(valWrapperType)) {
            BigDecimal bd = (BigDecimal)ce.getValue();
            if (ClassHelper.Float_TYPE.equals(returnWrapperType)) {
                return AnnotationConstantsVisitor.configure(val, new ConstantExpression(Float.valueOf(bd.floatValue()), true));
            }
            if (ClassHelper.Double_TYPE.equals(returnWrapperType)) {
                return AnnotationConstantsVisitor.configure(val, new ConstantExpression(bd.doubleValue(), true));
            }
        }
        return null;
    }

    private static Expression configure(Expression orig, Expression result) {
        result.setSourcePosition(orig);
        return result;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }
}

