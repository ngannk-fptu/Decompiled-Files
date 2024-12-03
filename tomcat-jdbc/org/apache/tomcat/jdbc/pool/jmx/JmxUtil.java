/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.jmx;

import java.lang.management.ManagementFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JmxUtil {
    private static final Log log = LogFactory.getLog(JmxUtil.class);

    public static ObjectName registerJmx(ObjectName base, String keyprop, Object obj) {
        ObjectName oname = null;
        try {
            oname = JmxUtil.getObjectName(base, keyprop);
            if (oname != null) {
                ManagementFactory.getPlatformMBeanServer().registerMBean(obj, oname);
            }
        }
        catch (Exception e) {
            log.error((Object)"Jmx registration failed.", (Throwable)e);
        }
        return oname;
    }

    public static void unregisterJmx(ObjectName oname) {
        if (oname == null) {
            return;
        }
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(oname);
        }
        catch (Exception e) {
            log.error((Object)"Jmx unregistration failed.", (Throwable)e);
        }
    }

    private static ObjectName getObjectName(ObjectName base, String keyprop) throws MalformedObjectNameException {
        if (base == null) {
            return null;
        }
        StringBuilder OnameStr = new StringBuilder(base.toString());
        if (keyprop != null) {
            OnameStr.append(keyprop);
        }
        ObjectName oname = new ObjectName(OnameStr.toString());
        return oname;
    }
}

