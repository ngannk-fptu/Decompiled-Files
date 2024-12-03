/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.classgen.asm.StatementMetaTypeChooser;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticTypesTypeChooser
extends StatementMetaTypeChooser {
    @Override
    public ClassNode resolveType(Expression exp, ClassNode current) {
        AnnotatedNode target = exp instanceof VariableExpression ? StaticTypesTypeChooser.getTarget((VariableExpression)exp) : exp;
        ClassNode inferredType = (ClassNode)target.getNodeMetaData((Object)StaticTypesMarker.DECLARATION_INFERRED_TYPE);
        if (inferredType == null && (inferredType = (ClassNode)target.getNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE)) == null && target instanceof VariableExpression && ((VariableExpression)target).getAccessedVariable() instanceof Parameter) {
            target = (Parameter)((VariableExpression)target).getAccessedVariable();
            inferredType = ((Parameter)target).getOriginType();
        }
        if (inferredType != null) {
            if (ClassHelper.VOID_TYPE == inferredType) {
                inferredType = super.resolveType(exp, current);
            }
            return inferredType;
        }
        if (target instanceof VariableExpression && ((VariableExpression)target).isThisExpression()) {
            return current;
        }
        return super.resolveType(exp, current);
    }

    private static VariableExpression getTarget(VariableExpression ve) {
        if (ve.getAccessedVariable() == null || ve.getAccessedVariable() == ve || !(ve.getAccessedVariable() instanceof VariableExpression)) {
            return ve;
        }
        return StaticTypesTypeChooser.getTarget((VariableExpression)ve.getAccessedVariable());
    }
}

