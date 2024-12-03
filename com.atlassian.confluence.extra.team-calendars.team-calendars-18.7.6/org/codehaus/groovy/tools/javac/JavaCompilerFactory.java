/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.tools.javac.JavaCompiler;

public interface JavaCompilerFactory {
    public JavaCompiler createCompiler(CompilerConfiguration var1);
}

