/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class CryptoServicesPermission
extends Permission {
    public static final String GLOBAL_CONFIG = "globalConfig";
    public static final String THREAD_LOCAL_CONFIG = "threadLocalConfig";
    public static final String DEFAULT_RANDOM = "defaultRandomConfig";
    public static final String CONSTRAINTS = "constraints";
    private final Set<String> actions = new HashSet<String>();

    public CryptoServicesPermission(String name) {
        super(name);
        this.actions.add(name);
    }

    @Override
    public boolean implies(Permission permission) {
        if (permission instanceof CryptoServicesPermission) {
            CryptoServicesPermission other = (CryptoServicesPermission)permission;
            if (this.getName().equals(other.getName())) {
                return true;
            }
            if (this.actions.containsAll(other.actions)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CryptoServicesPermission) {
            CryptoServicesPermission other = (CryptoServicesPermission)obj;
            if (this.actions.equals(other.actions)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.actions.hashCode();
    }

    @Override
    public String getActions() {
        return this.actions.toString();
    }
}

