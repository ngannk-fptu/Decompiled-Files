/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.BeanCreationException;

public class BeanIsAbstractException
extends BeanCreationException {
    public BeanIsAbstractException(String beanName) {
        super(beanName, "Bean definition is abstract");
    }
}

