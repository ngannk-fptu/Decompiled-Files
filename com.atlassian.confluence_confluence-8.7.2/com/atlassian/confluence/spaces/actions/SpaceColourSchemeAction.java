/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.admin.actions.lookandfeel.ColourSchemeAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.spaces.actions.SpaceAware;

public class SpaceColourSchemeAction
extends ColourSchemeAction
implements SpaceAdministrative,
SpaceAware {
    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
}

