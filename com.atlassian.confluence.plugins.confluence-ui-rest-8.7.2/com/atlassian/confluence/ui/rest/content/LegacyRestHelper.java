/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.ui.rest.content;

import com.atlassian.confluence.legacyapi.model.content.Label;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.ws.rs.core.Response;

@Deprecated
public class LegacyRestHelper {
    public static Map<String, Object> createFailureResultMap(IllegalArgumentException e) {
        return ImmutableMap.of((Object)"message", (Object)e.getMessage(), (Object)"status", (Object)Response.Status.FORBIDDEN.getStatusCode(), (Object)"success", (Object)false);
    }

    public static Map<String, Object> createSuccessResultMap(Iterable<Label> labels) {
        return ImmutableMap.of((Object)"labels", labels, (Object)"success", (Object)true);
    }
}

