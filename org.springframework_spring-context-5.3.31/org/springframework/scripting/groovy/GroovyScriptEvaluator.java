/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Binding
 *  groovy.lang.GroovyRuntimeException
 *  groovy.lang.GroovyShell
 *  org.codehaus.groovy.control.CompilerConfiguration
 *  org.codehaus.groovy.control.customizers.CompilationCustomizer
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.lang.Nullable
 */
package org.springframework.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import java.io.IOException;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

public class GroovyScriptEvaluator
implements ScriptEvaluator,
BeanClassLoaderAware {
    @Nullable
    private ClassLoader classLoader;
    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

    public GroovyScriptEvaluator() {
    }

    public GroovyScriptEvaluator(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setCompilerConfiguration(@Nullable CompilerConfiguration compilerConfiguration) {
        this.compilerConfiguration = compilerConfiguration != null ? compilerConfiguration : new CompilerConfiguration();
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return this.compilerConfiguration;
    }

    public void setCompilationCustomizers(CompilationCustomizer ... compilationCustomizers) {
        this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
    }

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
        GroovyShell groovyShell = new GroovyShell(this.classLoader, new Binding(arguments), this.compilerConfiguration);
        try {
            String filename;
            String string = filename = script instanceof ResourceScriptSource ? ((ResourceScriptSource)script).getResource().getFilename() : null;
            if (filename != null) {
                return groovyShell.evaluate(script.getScriptAsString(), filename);
            }
            return groovyShell.evaluate(script.getScriptAsString());
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access Groovy script", ex);
        }
        catch (GroovyRuntimeException ex) {
            throw new ScriptCompilationException(script, (Throwable)ex);
        }
    }
}

