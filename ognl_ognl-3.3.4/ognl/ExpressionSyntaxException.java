/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.OgnlException;

public class ExpressionSyntaxException
extends OgnlException {
    public ExpressionSyntaxException(String expression, Throwable reason) {
        super("Malformed OGNL expression: " + expression, reason);
    }
}

