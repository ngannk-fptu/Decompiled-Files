/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IGenericMethod;

public interface ISourceMethod
extends IGenericMethod {
    public int getDeclarationSourceEnd();

    public int getDeclarationSourceStart();

    public char[][] getExceptionTypeNames();

    public int getNameSourceEnd();

    public int getNameSourceStart();

    public char[] getReturnTypeName();

    public char[][] getTypeParameterNames();

    public char[][][] getTypeParameterBounds();
}

