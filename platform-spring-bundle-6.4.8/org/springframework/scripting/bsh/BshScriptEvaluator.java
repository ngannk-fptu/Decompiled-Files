/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bsh.EvalError
 *  bsh.Interpreter
 */
package org.springframework.scripting.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;

public class BshScriptEvaluator
implements ScriptEvaluator,
BeanClassLoaderAware {
    @Nullable
    private ClassLoader classLoader;

    public BshScriptEvaluator() {
    }

    public BshScriptEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    @Nullable
    public Object evaluate(ScriptSource script) {
        return this.evaluate(script, null);
    }

    @Override
    @Nullable
    public Object evaluate(ScriptSource script, @Nullable Map<String, Object> arguments) {
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(this.classLoader);
            if (arguments != null) {
                for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                    interpreter.set(entry.getKey(), entry.getValue());
                }
            }
            return interpreter.eval((Reader)new StringReader(script.getScriptAsString()));
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access BeanShell script", ex);
        }
        catch (EvalError ex) {
            throw new ScriptCompilationException(script, (Throwable)ex);
        }
    }
}

