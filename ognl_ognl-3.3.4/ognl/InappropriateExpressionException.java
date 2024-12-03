/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.Node;
import ognl.OgnlException;

public class InappropriateExpressionException
extends OgnlException {
    public InappropriateExpressionException(Node tree) {
        super("Inappropriate OGNL expression: " + tree);
    }
}

