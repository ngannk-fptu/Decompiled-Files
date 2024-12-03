/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.migration;

import com.atlassian.scheduler.caesium.migration.StringKeyDirtyFlagMap;

public class JobDataMap
extends StringKeyDirtyFlagMap {
    private static final long serialVersionUID = -6939901990106713909L;

    protected Object readResolve() {
        return this.unwrap().get("parameters");
    }
}

