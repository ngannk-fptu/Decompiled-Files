/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.event.events.Timestamped;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.types.UserDriven;
import java.io.Serializable;

public interface LikeEvent
extends Serializable,
UserDriven,
Contented,
Timestamped {
    public Object getSource();
}

