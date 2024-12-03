/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.LessCompiler
 *  com.atlassian.lesscss.RhinoLessCompiler
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.LessCompiler;
import com.atlassian.lesscss.RhinoLessCompiler;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class LessCompilerSupplier
implements Supplier<LessCompiler> {
    private final Supplier<LessCompiler> delegate = Suppliers.memoize((Supplier)new Supplier<LessCompiler>(){

        public LessCompiler get() {
            return new RhinoLessCompiler();
        }
    });

    public LessCompiler get() {
        return (LessCompiler)this.delegate.get();
    }
}

