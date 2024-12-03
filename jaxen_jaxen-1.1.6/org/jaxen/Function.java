/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.FunctionCallException;

public interface Function {
    public Object call(Context var1, List var2) throws FunctionCallException;
}

