/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.space;

import com.atlassian.confluence.core.service.SingleEntityLocator;
import com.atlassian.confluence.spaces.Space;

public interface SpaceLocator
extends SingleEntityLocator {
    public Space getSpace();
}

