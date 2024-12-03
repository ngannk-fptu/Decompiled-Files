/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Component
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.internal.rest.RestEntity;

public class RestComponent
extends RestEntity {
    public RestComponent(Component component) {
        this.put("id", component.getId());
        this.put("name", component.getName());
    }
}

