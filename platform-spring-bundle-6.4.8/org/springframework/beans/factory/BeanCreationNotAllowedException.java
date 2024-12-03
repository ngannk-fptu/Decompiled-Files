/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.BeanCreationException;

public class BeanCreationNotAllowedException
extends BeanCreationException {
    public BeanCreationNotAllowedException(String beanName, String msg) {
        super(beanName, msg);
    }
}

