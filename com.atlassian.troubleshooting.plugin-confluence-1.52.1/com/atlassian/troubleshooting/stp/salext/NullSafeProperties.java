/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.salext;

import java.util.Properties;

public class NullSafeProperties
extends Properties {
    @Override
    public synchronized Object put(Object key, Object value) {
        if (key != null && value != null) {
            return super.put(key, value);
        }
        return null;
    }
}

