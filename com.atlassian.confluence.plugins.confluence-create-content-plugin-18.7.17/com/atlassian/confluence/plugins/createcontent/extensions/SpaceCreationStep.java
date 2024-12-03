/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.spaces.Space;
import java.util.Map;

public interface SpaceCreationStep {
    public boolean prehandle(Map<String, Object> var1);

    public void posthandle(Space var1, Map<String, Object> var2);
}

