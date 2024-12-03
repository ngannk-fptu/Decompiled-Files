/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scripting;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;

public interface ScriptEvaluator {
    @Nullable
    public Object evaluate(ScriptSource var1) throws ScriptCompilationException;

    @Nullable
    public Object evaluate(ScriptSource var1, @Nullable Map<String, Object> var2) throws ScriptCompilationException;
}

