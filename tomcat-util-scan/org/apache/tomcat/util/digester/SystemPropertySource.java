/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils$SecurePropertySource
 *  org.apache.tomcat.util.security.PermissionCheck
 */
package org.apache.tomcat.util.digester;

import java.security.Permission;
import java.util.PropertyPermission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

public class SystemPropertySource
implements IntrospectionUtils.SecurePropertySource {
    public String getProperty(String key) {
        return this.getProperty(key, null);
    }

    public String getProperty(String key, ClassLoader classLoader) {
        PropertyPermission p;
        if (classLoader instanceof PermissionCheck && !((PermissionCheck)classLoader).check((Permission)(p = new PropertyPermission(key, "read")))) {
            return null;
        }
        return System.getProperty(key);
    }
}

