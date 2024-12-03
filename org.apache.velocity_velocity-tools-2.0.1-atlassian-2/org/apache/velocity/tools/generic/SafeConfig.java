/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Map;
import org.apache.velocity.tools.generic.ValueParser;

public class SafeConfig {
    public static final String LOCK_CONFIG_KEY = "lockConfig";
    @Deprecated
    public static final String OLD_LOCK_CONFIG_KEY = "lock-config";
    public static final String SAFE_MODE_KEY = "safeMode";
    private boolean configLocked = false;
    private boolean safeMode = false;

    protected void setLockConfig(boolean lock) {
        this.configLocked = lock;
    }

    protected void setSafeMode(boolean safe) {
        this.safeMode = safe;
    }

    public boolean isConfigLocked() {
        return this.configLocked;
    }

    public boolean isSafeMode() {
        return this.safeMode;
    }

    public void configure(Map params) {
        if (!this.isConfigLocked()) {
            ValueParser values = new ValueParser(params);
            this.configure(values);
            this.setSafeMode(values.getBoolean(SAFE_MODE_KEY, true));
            Boolean lock = values.getBoolean(LOCK_CONFIG_KEY);
            if (lock == null) {
                lock = values.getBoolean(OLD_LOCK_CONFIG_KEY, Boolean.TRUE);
            }
            this.setLockConfig(lock);
        }
    }

    protected void configure(ValueParser values) {
    }
}

