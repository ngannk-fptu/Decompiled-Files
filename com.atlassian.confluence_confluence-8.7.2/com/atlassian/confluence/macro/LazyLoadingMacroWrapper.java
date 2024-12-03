/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Map;

public class LazyLoadingMacroWrapper
implements Macro {
    private final LazyReference<Macro> delegate;

    public LazyLoadingMacroWrapper(LazyReference<Macro> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return ((Macro)this.delegate.get()).execute(parameters, body, context);
    }

    @Override
    public Macro.BodyType getBodyType() {
        return ((Macro)this.delegate.get()).getBodyType();
    }

    @Override
    public Macro.OutputType getOutputType() {
        return ((Macro)this.delegate.get()).getOutputType();
    }

    public Macro getMacro() {
        return (Macro)this.delegate.get();
    }
}

