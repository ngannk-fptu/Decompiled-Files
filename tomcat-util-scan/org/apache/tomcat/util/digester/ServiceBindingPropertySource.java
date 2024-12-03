/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils$SecurePropertySource
 *  org.apache.tomcat.util.security.PermissionCheck
 */
package org.apache.tomcat.util.digester;

import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

public class ServiceBindingPropertySource
implements IntrospectionUtils.SecurePropertySource {
    private static final String SERVICE_BINDING_ROOT_ENV_VAR = "SERVICE_BINDING_ROOT";

    public String getProperty(String key) {
        return null;
    }

    public String getProperty(String key, ClassLoader classLoader) {
        String[] parts;
        RuntimePermission p;
        if (classLoader instanceof PermissionCheck && !((PermissionCheck)classLoader).check((Permission)(p = new RuntimePermission("getenv.SERVICE_BINDING_ROOT", null)))) {
            return null;
        }
        String serviceBindingRoot = System.getenv(SERVICE_BINDING_ROOT_ENV_VAR);
        if (serviceBindingRoot == null) {
            return null;
        }
        boolean chomp = false;
        if (key.startsWith("chomp:")) {
            chomp = true;
            key = key.substring(6);
        }
        if ((parts = key.split("\\.")).length != 2) {
            return null;
        }
        Path path = Paths.get(serviceBindingRoot, parts[0], parts[1]);
        if (!path.toFile().exists()) {
            return null;
        }
        try {
            FilePermission p2;
            if (classLoader instanceof PermissionCheck && !((PermissionCheck)classLoader).check((Permission)(p2 = new FilePermission(path.toString(), "read")))) {
                return null;
            }
            byte[] bytes = Files.readAllBytes(path);
            int length = bytes.length;
            if (chomp) {
                byte c;
                if (length > 1 && bytes[length - 2] == 13 && bytes[length - 2] == 10) {
                    length -= 2;
                } else if (length > 0 && ((c = bytes[length - 1]) == 13 || c == 10)) {
                    --length;
                }
            }
            return new String(bytes, 0, length);
        }
        catch (IOException e) {
            return null;
        }
    }
}

