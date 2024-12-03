/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.tools.javac.JavaCompiler;
import org.codehaus.groovy.tools.javac.JavaCompilerFactory;
import org.codehaus.groovy.tools.javac.JavacJavaCompiler;

public class JavacCompilerFactory
implements JavaCompilerFactory {
    @Override
    public JavaCompiler createCompiler(CompilerConfiguration config) {
        return new JavacJavaCompiler(config);
    }
}

