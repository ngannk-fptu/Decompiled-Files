/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;

public interface Invitee
extends JsonSerializable,
Comparable<Invitee> {
    public String getId();

    public String getName();

    public String getEmail();

    public String getDisplayName();

    public String getType();

    public String getAvatarIconUrl();
}

