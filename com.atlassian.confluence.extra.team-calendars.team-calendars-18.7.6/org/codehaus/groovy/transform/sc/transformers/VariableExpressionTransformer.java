/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class VariableExpressionTransformer {
    public Expression transformVariableExpression(VariableExpression expr) {
        Expression trn = VariableExpressionTransformer.tryTransformPrivateFieldAccess(expr);
        if (trn != null) {
            return trn;
        }
        trn = VariableExpressionTransformer.tryTransformDelegateToProperty(expr);
        if (trn != null) {
            return trn;
        }
        return expr;
    }

    private static Expression tryTransformDelegateToProperty(VariableExpression expr) {
        Object val = expr.getNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER);
        if (val == null) {
            return null;
        }
        VariableExpression implicitThis = new VariableExpression("this");
        PropertyExpression pexp = new PropertyExpression((Expression)implicitThis, expr.getName());
        pexp.copyNodeMetaData(expr);
        pexp.setImplicitThis(true);
        pexp.getProperty().setSourcePosition(expr);
        ClassNode owner = (ClassNode)expr.getNodeMetaData((Object)StaticCompilationMetadataKeys.PROPERTY_OWNER);
        if (owner != null) {
            implicitThis.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, owner);
            implicitThis.putNodeMetaData((Object)StaticTypesMarker.IMPLICIT_RECEIVER, val);
        }
        return pexp;
    }

    private static Expression tryTransformPrivateFieldAccess(VariableExpression expr) {
        FieldNode field = (FieldNode)expr.getNodeMetaData((Object)StaticTypesMarker.PV_FIELDS_ACCESS);
        if (field == null) {
            field = (FieldNode)expr.getNodeMetaData((Object)StaticTypesMarker.PV_FIELDS_MUTATION);
        }
        if (field != null) {
            VariableExpression receiver = new VariableExpression("this");
            PropertyExpression pexp = new PropertyExpression((Expression)receiver, expr.getName());
            pexp.setImplicitThis(true);
            pexp.getProperty().setSourcePosition(expr);
            receiver.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, field.getDeclaringClass());
            pexp.putNodeMetaData((Object)StaticTypesMarker.INFERRED_TYPE, field.getOriginType());
            return pexp;
        }
        return null;
    }
}

