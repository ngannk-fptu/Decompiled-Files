/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;

public interface AspectJPrecedenceInformation
extends Ordered {
    public String getAspectName();

    public int getDeclarationOrder();

    public boolean isBeforeAdvice();

    public boolean isAfterAdvice();
}

