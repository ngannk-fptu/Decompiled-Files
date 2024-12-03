/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

public final class SecurityManagerUtil {
    private static final boolean isJava18OrHigher = JavaEnvUtils.isAtLeastJavaVersion("18");
    private static final boolean sysPropWarnOnSecMgrUsage = Boolean.getBoolean("ant.securitymanager.usage.warn");

    public static boolean isSetSecurityManagerAllowed() {
        return !isJava18OrHigher;
    }

    public static boolean warnOnSecurityManagerUsage(Project project) {
        if (project == null) {
            return sysPropWarnOnSecMgrUsage;
        }
        String val = project.getProperty("ant.securitymanager.usage.warn");
        if (val == null) {
            return sysPropWarnOnSecMgrUsage;
        }
        return Boolean.parseBoolean(val);
    }
}

