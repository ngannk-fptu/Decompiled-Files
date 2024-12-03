/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils$SecurePropertySource
 *  org.apache.tomcat.util.security.PermissionCheck
 */
package org.apache.tomcat.util.digester;

import java.security.Permission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

public class EnvironmentPropertySource
implements IntrospectionUtils.SecurePropertySource {
    public String getProperty(String key) {
        return null;
    }

    public String getProperty(String key, ClassLoader classLoader) {
        RuntimePermission p;
        if (classLoader instanceof PermissionCheck && !((PermissionCheck)classLoader).check((Permission)(p = new RuntimePermission("getenv." + key, null)))) {
            return null;
        }
        return System.getenv(key);
    }
}

