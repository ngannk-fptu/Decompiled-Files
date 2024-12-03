/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IGenericType;
import org.eclipse.jdt.internal.compiler.env.ISourceField;
import org.eclipse.jdt.internal.compiler.env.ISourceMethod;

public interface ISourceType
extends IGenericType {
    public int getDeclarationSourceEnd();

    public int getDeclarationSourceStart();

    public ISourceType getEnclosingType();

    public ISourceField[] getFields();

    public char[][] getInterfaceNames();

    default public char[][] getPermittedSubtypeNames() {
        return null;
    }

    public ISourceType[] getMemberTypes();

    public ISourceMethod[] getMethods();

    public char[] getName();

    public int getNameSourceEnd();

    public int getNameSourceStart();

    public char[] getSuperclassName();

    public char[][][] getTypeParameterBounds();

    public char[][] getTypeParameterNames();

    public boolean isAnonymous();
}

