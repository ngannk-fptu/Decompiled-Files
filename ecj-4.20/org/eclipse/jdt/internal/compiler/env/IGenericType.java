/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IDependent;

public interface IGenericType
extends IDependent {
    public int getModifiers();

    public boolean isBinaryType();
}

