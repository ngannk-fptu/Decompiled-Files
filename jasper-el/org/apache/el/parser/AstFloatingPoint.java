/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.math.BigDecimal;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstFloatingPoint
extends SimpleNode {
    private volatile Number number;

    public AstFloatingPoint(int id) {
        super(id);
    }

    public Number getFloatingPoint() {
        if (this.number == null) {
            try {
                Double d = Double.valueOf(this.image);
                this.number = d.isInfinite() || d.isNaN() ? new BigDecimal(this.image) : d;
            }
            catch (NumberFormatException e) {
                throw new ELException((Throwable)e);
            }
        }
        return this.number;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.getFloatingPoint();
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return this.getFloatingPoint().getClass();
    }
}

