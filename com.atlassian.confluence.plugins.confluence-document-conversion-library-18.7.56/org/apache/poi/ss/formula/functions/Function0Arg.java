/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version="6.0.0")
public interface Function0Arg
extends Function {
    public ValueEval evaluate(int var1, int var2);
}

