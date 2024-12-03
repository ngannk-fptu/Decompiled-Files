/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.hierarchicalroles;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;

public final class RoleHierarchyUtils {
    private RoleHierarchyUtils() {
    }

    public static String roleHierarchyFromMap(Map<String, List<String>> roleHierarchyMap) {
        Assert.notEmpty(roleHierarchyMap, (String)"roleHierarchyMap cannot be empty");
        StringWriter result = new StringWriter();
        PrintWriter writer = new PrintWriter(result);
        roleHierarchyMap.forEach((role, impliedRoles) -> {
            Assert.hasLength((String)role, (String)"role name must be supplied");
            Assert.notEmpty((Collection)impliedRoles, (String)"implied role name(s) cannot be empty");
            for (String impliedRole : impliedRoles) {
                writer.println(role + " > " + impliedRole);
            }
        });
        return result.toString();
    }
}

