/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.indy;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.classgen.asm.BinaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.WriterController;

public class IndyBinHelper
extends BinaryExpressionHelper {
    public IndyBinHelper(WriterController wc) {
        super(wc);
    }

    @Override
    protected void writePostOrPrefixMethod(int op, String method, Expression expression, Expression orig) {
        this.getController().getInvocationWriter().makeCall(orig, EmptyExpression.INSTANCE, new ConstantExpression(method), MethodCallExpression.NO_ARGUMENTS, InvocationWriter.invokeMethod, false, false, false);
    }
}

