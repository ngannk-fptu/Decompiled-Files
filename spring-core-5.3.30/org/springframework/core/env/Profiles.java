/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import java.util.function.Predicate;
import org.springframework.core.env.ProfilesParser;

@FunctionalInterface
public interface Profiles {
    public boolean matches(Predicate<String> var1);

    public static Profiles of(String ... profileExpressions) {
        return ProfilesParser.parse(profileExpressions);
    }
}

