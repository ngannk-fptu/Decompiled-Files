/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import org.springframework.core.env.PropertyResolver;

public interface Environment
extends PropertyResolver {
    public String[] getActiveProfiles();

    public String[] getDefaultProfiles();

    public boolean acceptsProfiles(String ... var1);
}

