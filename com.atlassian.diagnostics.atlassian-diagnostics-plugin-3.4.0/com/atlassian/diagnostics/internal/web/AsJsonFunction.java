/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.diagnostics.internal.web;

import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;

public class AsJsonFunction
implements SoyServerFunction<String> {
    private final ImmutableSet<Integer> VALID_ARG_SIZES = ImmutableSet.of((Object)1);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String apply(Object ... args) {
        try {
            return this.objectMapper.writeValueAsString(args[0]);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String getName() {
        return "as_json";
    }

    public Set<Integer> validArgSizes() {
        return this.VALID_ARG_SIZES;
    }
}

