/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IDependent;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;

public interface ICompilationUnit
extends IDependent {
    public char[] getContents();

    public char[] getMainTypeName();

    public char[][] getPackageName();

    default public boolean ignoreOptionalProblems() {
        return false;
    }

    default public ModuleBinding module(LookupEnvironment environment) {
        return environment.getModule(this.getModuleName());
    }

    default public char[] getModuleName() {
        return null;
    }

    default public String getDestinationPath() {
        return null;
    }

    default public String getExternalAnnotationPath(String qualifiedTypeName) {
        return null;
    }
}

