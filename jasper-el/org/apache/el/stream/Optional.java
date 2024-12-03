/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 *  javax.el.LambdaExpression
 */
package org.apache.el.stream;

import javax.el.ELException;
import javax.el.LambdaExpression;
import org.apache.el.util.MessageFactory;

public class Optional {
    private final Object obj;
    static final Optional EMPTY = new Optional(null);

    Optional(Object obj) {
        this.obj = obj;
    }

    public Object get() throws ELException {
        if (this.obj == null) {
            throw new ELException(MessageFactory.get("stream.optional.empty"));
        }
        return this.obj;
    }

    public void ifPresent(LambdaExpression le) {
        if (this.obj != null) {
            le.invoke(new Object[]{this.obj});
        }
    }

    public Object orElse(Object other) {
        if (this.obj == null) {
            return other;
        }
        return this.obj;
    }

    public Object orElseGet(Object le) {
        if (this.obj == null) {
            if (le instanceof LambdaExpression) {
                return ((LambdaExpression)le).invoke((Object[])null);
            }
            return le;
        }
        return this.obj;
    }
}

