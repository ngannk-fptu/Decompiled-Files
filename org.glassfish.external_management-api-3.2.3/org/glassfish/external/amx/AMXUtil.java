/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.amx;

import javax.management.ObjectName;
import org.glassfish.external.arc.Stability;
import org.glassfish.external.arc.Taxonomy;

@Taxonomy(stability=Stability.UNCOMMITTED)
public final class AMXUtil {
    private AMXUtil() {
    }

    public static ObjectName newObjectName(String s) {
        try {
            return new ObjectName(s);
        }
        catch (Exception e) {
            throw new RuntimeException("bad ObjectName", e);
        }
    }

    public static ObjectName newObjectName(String domain, String props) {
        return AMXUtil.newObjectName(domain + ":" + props);
    }

    public static ObjectName getMBeanServerDelegateObjectName() {
        return AMXUtil.newObjectName("JMImplementation:type=MBeanServerDelegate");
    }

    public static String prop(String key, String value) {
        return key + "=" + value;
    }
}

