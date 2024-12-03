/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IGenericField;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public interface IBinaryField
extends IGenericField {
    public IBinaryAnnotation[] getAnnotations();

    public IBinaryTypeAnnotation[] getTypeAnnotations();

    public Constant getConstant();

    public char[] getGenericSignature();

    public char[] getName();

    public long getTagBits();

    public char[] getTypeName();
}

