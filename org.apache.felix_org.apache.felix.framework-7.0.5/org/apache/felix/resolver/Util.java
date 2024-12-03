/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class Util {
    public static String getSymbolicName(Resource resource) {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps) {
            if (!cap.getNamespace().equals("osgi.identity")) continue;
            return cap.getAttributes().get("osgi.identity").toString();
        }
        return null;
    }

    public static Version getVersion(Resource resource) {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps) {
            if (!cap.getNamespace().equals("osgi.identity")) continue;
            return (Version)cap.getAttributes().get("version");
        }
        return null;
    }

    public static boolean isFragment(Resource resource) {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps) {
            if (!cap.getNamespace().equals("osgi.identity")) continue;
            String type = (String)cap.getAttributes().get("type");
            return type != null && type.equals("osgi.fragment");
        }
        return false;
    }

    public static boolean isOptional(Requirement req) {
        String resolution = req.getDirectives().get("resolution");
        return "optional".equalsIgnoreCase(resolution);
    }

    public static boolean isMultiple(Requirement req) {
        return "multiple".equals(req.getDirectives().get("cardinality")) && !Util.isDynamic(req);
    }

    public static boolean isDynamic(Requirement req) {
        return "dynamic".equals(req.getDirectives().get("resolution"));
    }

    public static boolean isReexport(Requirement req) {
        return "reexport".equals(req.getDirectives().get("visibility"));
    }

    public static List<Requirement> getDynamicRequirements(List<Requirement> reqs) {
        ArrayList<Requirement> result = new ArrayList<Requirement>();
        if (reqs != null) {
            for (Requirement req : reqs) {
                String resolution = req.getDirectives().get("resolution");
                if (resolution == null || !resolution.equals("dynamic")) continue;
                result.add(req);
            }
        }
        return result;
    }
}

