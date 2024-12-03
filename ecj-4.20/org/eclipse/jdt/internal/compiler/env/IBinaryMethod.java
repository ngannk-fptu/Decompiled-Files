/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IGenericMethod;

public interface IBinaryMethod
extends IGenericMethod {
    public IBinaryAnnotation[] getAnnotations();

    public Object getDefaultValue();

    public char[][] getExceptionTypeNames();

    public char[] getGenericSignature();

    public char[] getMethodDescriptor();

    public IBinaryAnnotation[] getParameterAnnotations(int var1, char[] var2);

    public int getAnnotatedParametersCount();

    public char[] getSelector();

    public long getTagBits();

    public boolean isClinit();

    public IBinaryTypeAnnotation[] getTypeAnnotations();
}

