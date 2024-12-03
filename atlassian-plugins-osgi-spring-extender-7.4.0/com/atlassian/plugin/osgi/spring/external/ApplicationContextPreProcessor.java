/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.context.ConfigurableApplicationContext
 */
package com.atlassian.plugin.osgi.spring.external;

import org.osgi.framework.Bundle;
import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextPreProcessor {
    public boolean isSpringPoweredBundle(Bundle var1);

    public void process(Bundle var1, ConfigurableApplicationContext var2);
}

