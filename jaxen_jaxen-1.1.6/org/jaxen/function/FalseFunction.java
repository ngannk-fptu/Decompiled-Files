/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public class FalseFunction
implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 0) {
            return FalseFunction.evaluate();
        }
        throw new FunctionCallException("false() requires no arguments.");
    }

    public static Boolean evaluate() {
        return Boolean.FALSE;
    }
}

