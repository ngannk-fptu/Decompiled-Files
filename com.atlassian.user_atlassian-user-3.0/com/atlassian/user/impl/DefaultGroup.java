/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.Group;
import com.atlassian.user.impl.DefaultEntity;

public class DefaultGroup
extends DefaultEntity
implements Group {
    public DefaultGroup() {
    }

    public DefaultGroup(String name) {
        super(name);
    }

    public String toString() {
        return this.getName();
    }
}

