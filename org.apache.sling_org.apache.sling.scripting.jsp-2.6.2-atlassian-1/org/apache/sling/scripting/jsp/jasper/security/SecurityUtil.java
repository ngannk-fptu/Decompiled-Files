/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.security;

import org.apache.sling.scripting.jsp.jasper.Constants;

public final class SecurityUtil {
    private static boolean packageDefinitionEnabled = System.getProperty("package.definition") != null;

    public static boolean isPackageProtectionEnabled() {
        return packageDefinitionEnabled && Constants.IS_SECURITY_ENABLED;
    }
}

