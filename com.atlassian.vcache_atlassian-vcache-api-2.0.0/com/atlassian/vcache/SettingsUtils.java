/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.vcache;

import com.atlassian.annotations.Internal;
import java.util.Optional;

@Internal
class SettingsUtils {
    SettingsUtils() {
    }

    static <T> Optional<T> ifPresent(Optional<T> first, Optional<T> second) {
        return first.isPresent() ? first : second;
    }
}

