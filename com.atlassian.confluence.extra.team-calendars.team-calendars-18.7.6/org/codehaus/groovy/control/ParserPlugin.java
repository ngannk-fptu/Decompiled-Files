/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.io.Reader;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.ParserException;
import org.codehaus.groovy.syntax.Reduction;

public interface ParserPlugin {
    public Reduction parseCST(SourceUnit var1, Reader var2) throws CompilationFailedException;

    public ModuleNode buildAST(SourceUnit var1, ClassLoader var2, Reduction var3) throws ParserException;
}

