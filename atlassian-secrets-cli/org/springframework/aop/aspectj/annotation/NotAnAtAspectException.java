/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.framework.AopConfigException;

public class NotAnAtAspectException
extends AopConfigException {
    private Class<?> nonAspectClass;

    public NotAnAtAspectException(Class<?> nonAspectClass) {
        super(nonAspectClass.getName() + " is not an @AspectJ aspect");
        this.nonAspectClass = nonAspectClass;
    }

    public Class<?> getNonAspectClass() {
        return this.nonAspectClass;
    }
}

