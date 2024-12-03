/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.migration;

import java.io.Serializable;
import java.util.Map;

public class DirtyFlagMap
implements Serializable {
    private static final long serialVersionUID = 1433884852607126222L;
    private boolean dirty = false;
    private Map<?, ?> map;

    Map<?, ?> unwrap() {
        return this.map;
    }
}

