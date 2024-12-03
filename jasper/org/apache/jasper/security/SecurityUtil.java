/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.security;

import org.apache.jasper.Constants;

public final class SecurityUtil {
    private static final boolean packageDefinitionEnabled = System.getProperty("package.definition") != null;

    public static boolean isPackageProtectionEnabled() {
        return packageDefinitionEnabled && Constants.IS_SECURITY_ENABLED;
    }
}

