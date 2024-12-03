/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.service.content.SpacePropertyService
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.service.content.SpacePropertyService;

@ExperimentalApi
public interface SpacePropertyServiceProvider {
    public SpacePropertyService get();
}

