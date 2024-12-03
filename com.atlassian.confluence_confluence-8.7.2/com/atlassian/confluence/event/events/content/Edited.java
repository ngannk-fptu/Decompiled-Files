/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.confluence.event.events.types.Updated;

public interface Edited
extends Updated {
    public boolean isMinorEdit();
}

