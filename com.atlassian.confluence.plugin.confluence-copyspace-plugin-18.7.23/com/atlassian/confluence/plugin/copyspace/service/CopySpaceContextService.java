/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;

public interface CopySpaceContextService {
    public CopySpaceContext createContext(CopySpaceRequest var1);
}

