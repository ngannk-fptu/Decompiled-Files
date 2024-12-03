/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.spaces.Space;
import java.util.Optional;

public interface SpaceService {
    public Space createNewSpace(CopySpaceContext var1);

    public void copySpaceWatchers(CopySpaceContext var1);

    public Space getSpace(String var1);

    public Optional<Long> getSpaceId(String var1);
}

