/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.context.ApplicationContext;

public interface ApplicationContextAware
extends Aware {
    public void setApplicationContext(ApplicationContext var1) throws BeansException;
}

