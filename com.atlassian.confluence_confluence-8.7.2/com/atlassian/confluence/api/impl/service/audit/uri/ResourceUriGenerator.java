/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface ResourceUriGenerator {
    public Map<String, URI> generate(URI var1, Set<String> var2);

    public static class NoopGenerator
    implements ResourceUriGenerator {
        @Override
        public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
            return Collections.emptyMap();
        }
    }
}

