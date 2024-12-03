/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

public interface ConfigurableWebEnvironment
extends ConfigurableEnvironment {
    public void initPropertySources(@Nullable ServletContext var1, @Nullable ServletConfig var2);
}

