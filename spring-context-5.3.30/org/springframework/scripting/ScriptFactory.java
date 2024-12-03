/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;

public interface ScriptFactory {
    public String getScriptSourceLocator();

    @Nullable
    public Class<?>[] getScriptInterfaces();

    public boolean requiresConfigInterface();

    @Nullable
    public Object getScriptedObject(ScriptSource var1, Class<?> ... var2) throws IOException, ScriptCompilationException;

    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource var1) throws IOException, ScriptCompilationException;

    public boolean requiresScriptedObjectRefresh(ScriptSource var1);
}

