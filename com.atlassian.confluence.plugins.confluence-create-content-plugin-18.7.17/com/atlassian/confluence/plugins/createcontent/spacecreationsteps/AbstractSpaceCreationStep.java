/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.spacecreationsteps;

import com.atlassian.confluence.plugins.createcontent.extensions.SpaceCreationStep;
import com.atlassian.confluence.spaces.Space;
import java.util.Map;

public abstract class AbstractSpaceCreationStep
implements SpaceCreationStep {
    @Override
    public boolean prehandle(Map<String, Object> context) {
        return true;
    }

    @Override
    public void posthandle(Space space, Map<String, Object> context) {
    }
}

