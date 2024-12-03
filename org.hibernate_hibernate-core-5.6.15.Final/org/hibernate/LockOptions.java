/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.LockMode;

public class LockOptions
implements Serializable {
    public static final LockOptions NONE = new LockOptions(LockMode.NONE);
    public static final LockOptions READ = new LockOptions(LockMode.READ);
    public static final LockOptions UPGRADE = new LockOptions(LockMode.UPGRADE);
    public static final int NO_WAIT = 0;
    public static final int WAIT_FOREVER = -1;
    public static final int SKIP_LOCKED = -2;
    private LockMode lockMode = LockMode.NONE;
    private int timeout = -1;
    private Map<String, LockMode> aliasSpecificLockModes;
    private Boolean followOnLocking;
    private boolean scope;

    public LockOptions() {
    }

    public LockOptions(LockMode lockMode) {
        this.lockMode = lockMode;
    }

    public LockMode getLockMode() {
        return this.lockMode;
    }

    public LockOptions setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    public LockOptions setAliasSpecificLockMode(String alias, LockMode lockMode) {
        if (this.aliasSpecificLockModes == null) {
            this.aliasSpecificLockModes = new LinkedHashMap<String, LockMode>();
        }
        this.aliasSpecificLockModes.put(alias, lockMode);
        return this;
    }

    public LockMode getAliasSpecificLockMode(String alias) {
        if (this.aliasSpecificLockModes == null) {
            return null;
        }
        return this.aliasSpecificLockModes.get(alias);
    }

    public LockMode getEffectiveLockMode(String alias) {
        LockMode lockMode = this.getAliasSpecificLockMode(alias);
        if (lockMode == null) {
            lockMode = this.lockMode;
        }
        return lockMode == null ? LockMode.NONE : lockMode;
    }

    public boolean hasAliasSpecificLockModes() {
        return this.aliasSpecificLockModes != null && !this.aliasSpecificLockModes.isEmpty();
    }

    public int getAliasLockCount() {
        if (this.aliasSpecificLockModes == null) {
            return 0;
        }
        return this.aliasSpecificLockModes.size();
    }

    public Iterator<Map.Entry<String, LockMode>> getAliasLockIterator() {
        return this.getAliasSpecificLocks().iterator();
    }

    public Iterable<Map.Entry<String, LockMode>> getAliasSpecificLocks() {
        if (this.aliasSpecificLockModes == null) {
            return Collections.emptyList();
        }
        return this.aliasSpecificLockModes.entrySet();
    }

    public LockMode findGreatestLockMode() {
        LockMode lockModeToUse = this.getLockMode();
        if (lockModeToUse == null) {
            lockModeToUse = LockMode.NONE;
        }
        if (this.aliasSpecificLockModes == null) {
            return lockModeToUse;
        }
        for (LockMode lockMode : this.aliasSpecificLockModes.values()) {
            if (!lockMode.greaterThan(lockModeToUse)) continue;
            lockModeToUse = lockMode;
        }
        return lockModeToUse;
    }

    public int getTimeOut() {
        return this.timeout;
    }

    public LockOptions setTimeOut(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public boolean getScope() {
        return this.scope;
    }

    public LockOptions setScope(boolean scope) {
        this.scope = scope;
        return this;
    }

    public Boolean getFollowOnLocking() {
        return this.followOnLocking;
    }

    public LockOptions setFollowOnLocking(Boolean followOnLocking) {
        this.followOnLocking = followOnLocking;
        return this;
    }

    public LockOptions makeCopy() {
        LockOptions copy = new LockOptions();
        LockOptions.copy(this, copy);
        return copy;
    }

    public static LockOptions copy(LockOptions source, LockOptions destination) {
        destination.setLockMode(source.getLockMode());
        destination.setScope(source.getScope());
        destination.setTimeOut(source.getTimeOut());
        if (source.aliasSpecificLockModes != null) {
            destination.aliasSpecificLockModes = new HashMap<String, LockMode>(source.aliasSpecificLockModes);
        }
        destination.setFollowOnLocking(source.getFollowOnLocking());
        return destination;
    }

    public static LockOptions interpret(LockMode lockMode) {
        if (lockMode == null || lockMode == LockMode.NONE) {
            return NONE;
        }
        if (lockMode == LockMode.READ) {
            return READ;
        }
        if (lockMode.greaterThan(LockMode.UPGRADE_NOWAIT)) {
            return UPGRADE;
        }
        return new LockOptions(lockMode);
    }
}

