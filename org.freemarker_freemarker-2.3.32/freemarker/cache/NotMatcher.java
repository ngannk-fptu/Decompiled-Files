/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateSourceMatcher;
import java.io.IOException;

public class NotMatcher
extends TemplateSourceMatcher {
    private final TemplateSourceMatcher matcher;

    public NotMatcher(TemplateSourceMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        return !this.matcher.matches(sourceName, templateSource);
    }
}

