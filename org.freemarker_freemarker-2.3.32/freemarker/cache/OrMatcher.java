/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateSourceMatcher;
import java.io.IOException;

public class OrMatcher
extends TemplateSourceMatcher {
    private final TemplateSourceMatcher[] matchers;

    public OrMatcher(TemplateSourceMatcher ... matchers) {
        if (matchers.length == 0) {
            throw new IllegalArgumentException("Need at least 1 matcher, had 0.");
        }
        this.matchers = matchers;
    }

    @Override
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        for (TemplateSourceMatcher matcher : this.matchers) {
            if (!matcher.matches(sourceName, templateSource)) continue;
            return true;
        }
        return false;
    }
}

