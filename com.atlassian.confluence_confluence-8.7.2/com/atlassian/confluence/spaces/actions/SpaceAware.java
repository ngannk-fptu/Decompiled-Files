/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;

public interface SpaceAware
extends Spaced {
    public void setSpace(Space var1);

    public boolean isSpaceRequired();

    public boolean isViewPermissionRequired();
}

