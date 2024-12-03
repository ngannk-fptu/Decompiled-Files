/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.classgen.asm.OptimizingStatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;

public class StatementMetaTypeChooser
implements TypeChooser {
    @Override
    public ClassNode resolveType(Expression exp, ClassNode current) {
        if (exp instanceof ClassExpression) {
            return ClassHelper.CLASS_Type;
        }
        OptimizingStatementWriter.StatementMeta meta = (OptimizingStatementWriter.StatementMeta)exp.getNodeMetaData(OptimizingStatementWriter.StatementMeta.class);
        ClassNode type = null;
        if (meta != null) {
            type = meta.type;
        }
        if (type != null) {
            return type;
        }
        if (exp instanceof VariableExpression) {
            FieldNode fn;
            VariableExpression ve = (VariableExpression)exp;
            if (ve.isClosureSharedVariable()) {
                return ve.getType();
            }
            type = ve.getOriginType();
            if (ve.getAccessedVariable() instanceof FieldNode && !(fn = (FieldNode)ve.getAccessedVariable()).getDeclaringClass().equals(current)) {
                return fn.getOriginType();
            }
        } else if (exp instanceof Variable) {
            Variable v = (Variable)((Object)exp);
            type = v.getOriginType();
        } else {
            type = exp.getType();
        }
        return type.redirect();
    }
}

