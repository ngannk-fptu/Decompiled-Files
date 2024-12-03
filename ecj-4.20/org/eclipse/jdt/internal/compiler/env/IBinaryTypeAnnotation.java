/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public interface IBinaryTypeAnnotation {
    public static final int[] NO_TYPE_PATH = new int[0];

    public IBinaryAnnotation getAnnotation();

    public int getTargetType();

    public int[] getTypePath();

    public int getSupertypeIndex();

    public int getTypeParameterIndex();

    public int getBoundIndex();

    public int getMethodFormalParameterIndex();

    public int getThrowsTypeIndex();
}

