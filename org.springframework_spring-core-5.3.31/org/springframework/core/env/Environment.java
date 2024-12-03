/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import org.springframework.core.env.Profiles;
import org.springframework.core.env.PropertyResolver;

public interface Environment
extends PropertyResolver {
    public String[] getActiveProfiles();

    public String[] getDefaultProfiles();

    default public boolean matchesProfiles(String ... profileExpressions) {
        return this.acceptsProfiles(Profiles.of(profileExpressions));
    }

    @Deprecated
    public boolean acceptsProfiles(String ... var1);

    public boolean acceptsProfiles(Profiles var1);
}

