/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import groovy.lang.GroovyClassLoader;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.control.CompilationUnit;

public class ASTTransformationsContext {
    protected final GroovyClassLoader transformLoader;
    protected final CompilationUnit compilationUnit;
    protected final Set<String> globalTransformNames = new HashSet<String>();

    public ASTTransformationsContext(CompilationUnit compilationUnit, GroovyClassLoader transformLoader) {
        this.compilationUnit = compilationUnit;
        this.transformLoader = transformLoader;
    }

    public CompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }

    public Set<String> getGlobalTransformNames() {
        return this.globalTransformNames;
    }

    public GroovyClassLoader getTransformLoader() {
        return this.transformLoader;
    }
}

