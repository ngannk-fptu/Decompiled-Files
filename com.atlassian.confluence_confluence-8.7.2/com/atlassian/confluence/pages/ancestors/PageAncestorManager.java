/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.pages.ancestors.AncestorRebuildException;
import com.atlassian.confluence.spaces.Space;

public interface PageAncestorManager {
    public void rebuildAll() throws AncestorRebuildException;

    public void rebuildSpace(Space var1) throws AncestorRebuildException;
}

