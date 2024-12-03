/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.JWSSignerOption;
import java.util.Set;

public class OptionUtils {
    public static <T extends JWSSignerOption> boolean optionIsPresent(Set<JWSSignerOption> opts, Class<T> tClass) {
        if (opts == null || opts.isEmpty()) {
            return false;
        }
        for (JWSSignerOption o : opts) {
            if (!o.getClass().isAssignableFrom(tClass)) continue;
            return true;
        }
        return false;
    }
}

