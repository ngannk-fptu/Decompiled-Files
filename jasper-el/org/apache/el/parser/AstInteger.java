/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.math.BigInteger;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstInteger
extends SimpleNode {
    private volatile Number number;

    public AstInteger(int id) {
        super(id);
    }

    protected Number getInteger() {
        if (this.number == null) {
            try {
                try {
                    this.number = Long.valueOf(this.image);
                }
                catch (NumberFormatException ignore) {
                    this.number = new BigInteger(this.image);
                }
            }
            catch (ArithmeticException | NumberFormatException e) {
                throw new ELException((Throwable)e);
            }
        }
        return this.number;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return this.getInteger().getClass();
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.getInteger();
    }
}

