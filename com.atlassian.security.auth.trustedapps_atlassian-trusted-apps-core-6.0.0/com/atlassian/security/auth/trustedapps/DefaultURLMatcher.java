/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.URLMatcher;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultURLMatcher
implements URLMatcher {
    private final Set<String> patterns;

    public DefaultURLMatcher(Set<String> patterns) {
        this.patterns = Collections.unmodifiableSet(new LinkedHashSet<String>(patterns));
    }

    @Override
    public boolean match(String urlPath) {
        if (this.patterns.isEmpty()) {
            return true;
        }
        for (String pattern : this.patterns) {
            if (!urlPath.startsWith(pattern)) continue;
            return true;
        }
        return false;
    }
}

