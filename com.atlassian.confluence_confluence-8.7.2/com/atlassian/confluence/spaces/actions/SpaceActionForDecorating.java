/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import java.util.Objects;

public final class SpaceActionForDecorating
extends AbstractSpaceAction {
    public SpaceActionForDecorating(Space space) {
        this.space = Objects.requireNonNull(space);
        this.key = Objects.requireNonNull(space.getKey());
    }
}

