/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Method;
import org.aspectj.lang.reflect.CodeSignature;

public interface AdviceSignature
extends CodeSignature {
    public Class getReturnType();

    public Method getAdvice();
}

