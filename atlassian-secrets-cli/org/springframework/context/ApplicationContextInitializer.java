/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    public void initialize(C var1);
}

