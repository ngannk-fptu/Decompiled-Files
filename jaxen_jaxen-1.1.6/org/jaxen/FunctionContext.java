/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen;

import org.jaxen.Function;
import org.jaxen.UnresolvableException;

public interface FunctionContext {
    public Function getFunction(String var1, String var2, String var3) throws UnresolvableException;
}

