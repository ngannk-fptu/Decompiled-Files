/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.migration.agent.rest;

import javax.ws.rs.core.UriBuilder;

public class UriHelper {
    private UriHelper() {
        throw new IllegalStateException("UriHelper class");
    }

    public static UriBuilder addParamToAnchor(UriBuilder builder, String key, String value) {
        String existingFrag = builder.build(new Object[0]).getFragment();
        if (existingFrag == null) {
            existingFrag = "";
        }
        String newFrag = UriBuilder.fromUri((String)existingFrag).queryParam(key, new Object[]{value}).build(new Object[0]).toString();
        return builder.fragment(newFrag);
    }
}

