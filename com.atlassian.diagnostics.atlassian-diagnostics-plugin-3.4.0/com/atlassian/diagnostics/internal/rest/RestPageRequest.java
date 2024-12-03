/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PageRequest
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.internal.rest.RestEntity;
import java.util.Objects;

public class RestPageRequest
extends RestEntity {
    public RestPageRequest(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "pageRequest");
        int start = pageRequest.getStart();
        if (start > 0) {
            this.put("start", start);
        }
        this.put("limit", pageRequest.getLimit());
    }
}

