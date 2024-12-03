/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.restapi.examples;

import com.atlassian.confluence.plugins.restapi.enrich.StaticEnricherFilter;
import java.util.Collections;
import java.util.Map;

public class WatchExamples {
    public static final Object WATCHING_EXAMPLE = StaticEnricherFilter.enrichResponse(WatchExamples.makeResponseExample());

    private static Map<String, Boolean> makeResponseExample() {
        return Collections.singletonMap("watching", true);
    }
}

