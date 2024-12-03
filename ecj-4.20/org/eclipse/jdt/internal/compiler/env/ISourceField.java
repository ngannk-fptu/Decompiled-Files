/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IGenericField;

public interface ISourceField
extends IGenericField {
    public int getDeclarationSourceEnd();

    public int getDeclarationSourceStart();

    public char[] getInitializationSource();

    public int getNameSourceEnd();

    public int getNameSourceStart();

    public char[] getTypeName();
}

